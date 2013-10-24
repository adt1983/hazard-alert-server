package com.hazardalert;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.CapUtil;
import com.google.publicalerts.cap.CapValidator;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Group;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;

@SuppressWarnings("serial")
public class TestServlet extends BaseServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		Fips philly = ofy().load().type(Fips.class).id("42101").safeGet();
		logger.info("Philly:\n");
		Polygon poly = philly.toPolygon();
		for (Point point : poly.getPointList()) {
			logger.info("Lat: " + point.getLatitude() + "\tLng: " + point.getLongitude() + "\n");
		}
		Queue queue = QueueFactory.getQueue("ingest");
		Alert alert = buildAlert();
		TaskOptions task = IngestAlertServlet.buildTaskXml(new CapXmlBuilder().toXml(alert));
		queue.add(task);
		setSuccess(resp);
	}

	protected Alert buildAlert() {
		Alert.Builder builder = Alert.newBuilder();
		builder.setXmlns(CapValidator.CAP_LATEST_XMLNS) //
				.setIdentifier("HAZARD-ALERT.APPSPOT.COM-TEST-" + Long.toString(new Date().getTime()))
				.setSender("hsas@dhs.gov")
				.setSent(CapUtil.formatCapDate(Calendar.getInstance()))
				.setStatus(Alert.Status.TEST)
				.setMsgType(Alert.MsgType.ALERT)
				.setSource("a source")
				.setScope(Alert.Scope.PUBLIC)
				.setAddresses(Group.newBuilder().addValue("address 1").addValue("address2").build())
				.addCode("abcde")
				.addCode("fghij")
				.setNote("TEST ALERT FROM HAZARD-ALERT.APPSPOT.COM - PLEASE DISREGARD")
				.addInfo(buildInfo());
		return builder.build();
	}

	protected Info buildInfo() {
		Calendar now = Calendar.getInstance();
		Calendar oneHourFromNow = Calendar.getInstance();
		oneHourFromNow.add(Calendar.HOUR, 1);
		Info.Builder builder = Info.newBuilder();
		builder.addCategory(Info.Category.SECURITY) //
				.addCategory(Info.Category.SAFETY)
				.setEvent("Homeland Security Advisory System Update")
				.setUrgency(Info.Urgency.FUTURE)
				.setSeverity(Info.Severity.MODERATE)
				.setCertainty(Info.Certainty.LIKELY)
				.setEffective(CapUtil.formatCapDate(now))
				.setExpires(CapUtil.formatCapDate(oneHourFromNow))
				.setSenderName("Department of Homeland Security")
				.setHeadline("Homeland Security Sets Code ORANGE")
				.setDescription("DHS has set the threat level to ORANGE.")
				.setInstruction("Take Protective Measures.")
				.setWeb("http://www.dhs.gov/dhspublic/display?theme=29")
				.addArea(buildArea());
		return builder.build();
	}

	protected Area buildArea() {
		Area.Builder builder = Area.newBuilder();
		builder.setAreaDesc("Laurentine Abyss") //
				.addCircle(Circle.newBuilder()
									.setPoint(Point.newBuilder().setLatitude(42.31).setLongitude(-55.86).build())
									.setRadius(1.0)
									.build());
		/*
				.addPolygon(Polygon.newBuilder()
									.addPoint(Point.newBuilder().setLatitude(39.95).setLongitude(-75.157).build())
									.addPoint(Point.newBuilder().setLatitude(39.95).setLongitude(-75.139).build())
									.addPoint(Point.newBuilder().setLatitude(39.94).setLongitude(-75.139).build())
									.addPoint(Point.newBuilder().setLatitude(39.94).setLongitude(-75.157).build())
									.addPoint(Point.newBuilder().setLatitude(39.95).setLongitude(-75.157).build())
									.build());
									*/
		return builder.build();
	}
}
