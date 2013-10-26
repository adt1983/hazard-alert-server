package com.hazardalert;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.publicalerts.cap.Alert.MsgType;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.CapException.Reason;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.ValuePair;
import com.google.publicalerts.cap.feed.CapFeedParser;
import com.google.publicalerts.cap.profile.GoogleProfile;
import com.hazardalert.common.CommonUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/*
	<headline>Tornado Warning issued May 20 at 2:40PM CDT until May 20 at 3:15PM CDT by NWS Norman-Oklahoma City</headline>
	<description>THE NATIONAL WEATHER SERVICE IN NORMAN HAS ISSUED A
 	* TORNADO WARNING FOR...
	NORTHWESTERN MCCLAIN COUNTY IN CENTRAL OKLAHOMA...
	SOUTHERN OKLAHOMA COUNTY IN CENTRAL OKLAHOMA...
	NORTHEASTERN GRADY COUNTY IN CENTRAL OKLAHOMA...
	NORTHERN CLEVELAND COUNTY IN CENTRAL OKLAHOMA...
 	* UNTIL 315 PM CDT
 	* AT 238 PM CDT...NATIONAL WEATHER SERVICE METEOROLOGISTS DETECTED A
	SEVERE THUNDERSTORM CAPABLE OF PRODUCING A TORNADO. THIS DANGEROUS
	STORM WAS LOCATED NEAR NEWCASTLE...AND MOVING EAST AT 20 MPH.
	IN ADDITION TO A TORNADO...LARGE DAMAGING HAIL UP TO GOLF BALL SIZE
	IS EXPECTED WITH THIS STORM.
 	* LOCATIONS IMPACTED INCLUDE...
	NORMAN...MOORE...NEWCASTLE...BRIDGE CREEK AND VALLEY BROOK.</description>
	<instruction>TAKE COVER NOW IN A STORM SHELTER OR AN INTERIOR ROOM OF A STURDY
	BUILDING. STAY AWAY FROM DOORS AND WINDOWS.</instruction>
	
	-- NOAA-NWS-ALERTS-OK124F00815BD0.TornadoWarning.124F0081791COK.OUNTOROUN.cf2dad7a6fa2871404d374f1e1b96193
	http://www.cnn.com/interactive/2013/05/us/moore-oklahoma-tornado/?hpt=hp_t2
 */
@SuppressWarnings("serial")
public class IngestAlertServlet extends TaskServlet {
	static final String PARAMETER_CAPURL = "capUrl";

	static final String PARAMETER_CAPXML = "capXml";

	private final CapFeedParser parser;

	public static TaskOptions buildTask(String capUrl) {
		return TaskOptions.Builder.withUrl("/ingestAlert").param(PARAMETER_CAPURL, capUrl).method(Method.POST);
	}

	public static TaskOptions buildTaskXml(String capXml) {
		return TaskOptions.Builder.withUrl("/ingestAlert").param(PARAMETER_CAPXML, capXml).method(Method.POST);
	}

	public IngestAlertServlet() {
		this.parser = new CapFeedParser(true);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		setStatusDone(resp);
		try {
			String capUrl = req.getParameter(PARAMETER_CAPURL);
			String capXml = req.getParameter(PARAMETER_CAPXML);
			if (capUrl == null && capXml == null) {
				throw new RuntimeException("No parameters.");
			}
			if (capUrl != null && capXml != null) {
				throw new RuntimeException("Both parameters set.");
			}
			if (null != capUrl) {
				logger.info("Fetching: " + capUrl);
				capXml = Util.loadUrl(capUrl);
			}
			if (capXml == null) {
				throw new RuntimeException("capXml == null");
			}
			capXml = capXml.trim().replaceFirst("^([\\W]+)<", "<"); // http://stackoverflow.com/questions/3030903/content-is-not-allowed-in-prolog-when-parsing-perfectly-valid-xml-on-gae
			logger.info("Ingesting:\n" + capXml);
			com.google.publicalerts.cap.Alert external = parser.parseAlert(capXml); // rethrow FatalException if this throws?
			com.google.publicalerts.cap.Alert internal = rewriteAlert(external);
			logger.info("Internal: \n" + new CapXmlBuilder().toXml(internal));
			GoogleProfile googleProfile = new GoogleProfile();
			List<Reason> errors = googleProfile.checkForErrors(internal);
			List<Reason> warnings = googleProfile.checkForRecommendations(internal);
			for (Reason e : errors) {
				logger.warning("Google Error: " + e.getMessage());
			}
			for (Reason w : warnings) {
				logger.warning("Google Warning: " + w.getMessage());
			}
			if (!isUseable(internal)) {
				return;
			}
			Alert alert = new Alert(internal);
			Alert existingAlert = Alert.find(alert.getFullName());
			if (null != existingAlert) {
				// Alert with this fullName already exists, check that they are exact duplicates?
				logger.warning("Already exists.");
				return;
			}
			/* Valid Alert. Persist. Check for updates. Push. 
			 */
			EntityManager em = ApiKeyInitializer.createEntityManager();
			try {
				em.getTransaction().begin();
				em.persist(alert);
				if (internal.getMsgType() == MsgType.CANCEL || internal.getMsgType() == MsgType.UPDATE) {
					for (String superceded : internal.getReferences().getValueList()) {
						logger.info("Supercede:\nnew: " + alert.getFullName() + "\nold: " + superceded);
						List<Alert> toUpdate = em.createQuery("FROM Alert WHERE fullName = :fullName AND updatedBy IS NULL", Alert.class)
													.setParameter("fullName", superceded)
													.getResultList();
						for (Alert updated : Util.returnList(toUpdate)) {
							updated.setUpdatedBy(alert);
							em.merge(updated);
						}
					}
				}
				em.getTransaction().commit();
				DB.pushAlert(alert);
			}
			finally {
				em.close();
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to ingest alert.", e);
			setStatusRetry(resp);
			super.doPost(req, resp); // don't retry if we are over the retry limit
		}
	}

	// TODO we need an algorithm that allows increases but doesn't allow reductions in the area of the input polygon
	private com.google.publicalerts.cap.Polygon simplify(com.google.publicalerts.cap.Polygon polygon) {
		if (polygon.getPointCount() < 20) {
			return polygon;
		}
		Polygon in = CommonUtil.toPolygonJts(polygon);
		Polygon out = (Polygon) com.vividsolutions.jts.simplify.TopologyPreservingSimplifier.simplify(in, 0.01);
		if (!in.equalsExact(out)) {
			logger.info("Simplification:\nin: " + in.toText() + "\nout: " + out.toText());
		}
		return CommonUtil.toPolygonCap(out);
	}

	private List<com.google.publicalerts.cap.Polygon> mergePolygons(List<com.google.publicalerts.cap.Polygon> input) {
		GeometryFactory factory = new GeometryFactory();
		GeometryCollection jtsInput = factory.createGeometryCollection(CommonUtil.cap_to_jts(input));
		List<Polygon> merged = new ArrayList<Polygon>();
		try {
			for (int i = 0; i < jtsInput.getNumGeometries(); i++) {
				Geometry g = jtsInput.getGeometryN(i);
				if (!(g instanceof Polygon)) {
					throw new RuntimeException("Not polygon. " + g.toText());
				}
				merged.add((Polygon) g);
			}
			//merge
			boolean done;
			do {
				done = true;
				for (int i = 0; i < merged.size(); i++) {
					Polygon a = merged.get(i);
					for (int j = i + 1; j < merged.size();) {
						final Polygon b = merged.get(j);
						if (a.overlaps(b) || a.contains(b) || a.within(b) || a.relate(b, "****1****")) { // don't merge on a single point (creates a MultiPolygon)
							Geometry u = a.union(b);
							if (!(u instanceof Polygon)) {
								throw new RuntimeException("Merged to non-polygon:\n" + "a: " + a.toText() + "\nb: " + b.toText() + "\nu: "
										+ u.toText());
							}
							merged.set(i, CommonUtil.toPolygonJts(CommonUtil.toPolygonCap(((Polygon) u).getExteriorRing())));
							a = merged.get(i);
							merged.remove(j);
							done = false;
						}
						else {
							j++;
						}
					}
				}
			}
			while (!done);
			List<com.google.publicalerts.cap.Polygon> output = new LinkedList<com.google.publicalerts.cap.Polygon>();
			for (Polygon p : merged) {
				output.add(CommonUtil.toPolygonCap(p));
			}
			return output;
		}
		finally {
			StringBuffer log = new StringBuffer();
			log.append(jtsInput.toText() + "\n");
			for (Polygon p : merged) {
				log.append("\n" + p.toText());
			}
			logger.info(log.toString());
		}
	}

	private com.google.publicalerts.cap.Alert rewriteAlert(com.google.publicalerts.cap.Alert in) {
		com.google.publicalerts.cap.Alert.Builder builder = in.toBuilder();
		for (Info.Builder info : builder.getInfoBuilderList()) {
			if (!info.hasLanguage()) {
				info.setLanguage("en-US");
			}
			if (!info.hasEffective()) {
				info.setEffective(in.getSent());
			}
			if (!info.hasExpires()) {
				Calendar expires = Calendar.getInstance();
				expires.add(Calendar.DAY_OF_YEAR, 1); // one day from now
				info.setExpires(DatatypeConverter.printTime(expires));
			}
			if (!info.hasSenderName()) {
				info.setSenderName(in.getSender());
			}
			info.clearParameter(); // strip params b/c we don't (yet) know how to handle them downstream
			for (Area.Builder area : info.getAreaBuilderList()) {
				/*
				 * Convert everything to a <polygon>
				 */
				List<com.google.publicalerts.cap.Polygon> polygons = new LinkedList<com.google.publicalerts.cap.Polygon>();
				for (Circle circle : area.getCircleList()) {
					if (circle.getRadius() > 0.001) { // > 1m
						polygons.add(CommonUtil.toPolygon(circle));
					}
				}
				area.clearCircle();
				for (com.google.publicalerts.cap.Polygon polygon : area.getPolygonList()) {
					polygons.add(simplify(polygon));
				}
				area.clearPolygon();
				/*
				 * If an alert originator has given us an explicit geometry (bless their heart) ignore any <geocode>
				 * Otherwise, try to generate a <polygon> from a <geocode>
				 * This breaks compliance with the standard since technically we should union everything
				 * https://groups.google.com/forum/#!msg/google-cap-community/12-0n51eXio/6L4gr19U7DAJ
				 */
				if (polygons.isEmpty()) {
					for (ValuePair geocode : area.getGeocodeList()) {
						if (geocode.getValueName().equals("FIPS6")) { //attempt to generate area based on fips code
							for (String fips : geocode.getValue().split(" ")) {
								if (6 == fips.length()) {
									fips = fips.substring(1); // NWS prepends '0' to their fips codes
								}
								if (5 == fips.length()) {
									com.google.publicalerts.cap.Polygon polygon = ofy().load()
																						.type(Fips.class)
																						.id(fips)
																						.safeGet()
																						.toPolygon();
									polygons.add(polygon);
								}
							}
						}
					}
				}
				area.clearGeocode();
				area.addAllPolygon(mergePolygons(polygons));
			}
			if (info.getAreaCount() > 1) {
				// Union area.
				Area.Builder areaUnion = Area.newBuilder();
				StringBuffer areaDescUnion = null;
				List<com.google.publicalerts.cap.Polygon> polygonUnion = new LinkedList<com.google.publicalerts.cap.Polygon>();
				for (Area area : info.getAreaList()) {
					if (null == areaDescUnion) {
						areaDescUnion = new StringBuffer();
						areaDescUnion.append(area.getAreaDesc());
					}
					else {
						areaDescUnion.append(", " + area.getAreaDesc());
					}
					for (com.google.publicalerts.cap.Polygon polygon : area.getPolygonList()) {
						polygonUnion.add(polygon);
					}
				}
				areaUnion.setAreaDesc(areaDescUnion.toString()); // This is going to piss somebody off.
				areaUnion.addAllPolygon(mergePolygons(polygonUnion));
				info.clearArea();
				info.addArea(areaUnion.build());
			}
		}
		return builder.build();
	}

	private boolean isUseable(com.google.publicalerts.cap.Alert impl) {
		if (impl.getScope() != com.google.publicalerts.cap.Alert.Scope.PUBLIC) {
			logger.warning("scope != PUBLIC");
			return false;
		}
		// No info
		if (impl.getInfoCount() == 0) {
			logger.warning("No info");
			return false;
		}
		// 1+ circle/polygon
		boolean hasArea = false;
		for (com.google.publicalerts.cap.Info i : impl.getInfoList()) {
			if (i.getAreaCount() > 0) {
				for (com.google.publicalerts.cap.Area area : i.getAreaList()) {
					if (area.getCircleCount() + area.getPolygonCount() > 0) {
						hasArea = true;
						break;
					}
				}
			}
		}
		if (!hasArea) {
			logger.warning("No Area");
			return false;
		}
		// past alert?
		boolean actionRequired = false;
		for (com.google.publicalerts.cap.Info i : impl.getInfoList()) {
			if (i.getUrgency() == Info.Urgency.IMMEDIATE || i.getUrgency() == Info.Urgency.EXPECTED
					|| i.getUrgency() == Info.Urgency.FUTURE || i.getUrgency() == Info.Urgency.UNKNOWN_URGENCY) {
				actionRequired = true;
				break;
			}
		}
		if (!actionRequired) {
			logger.warning("No action required");
			return false;
		}
		return true;
	}
}
