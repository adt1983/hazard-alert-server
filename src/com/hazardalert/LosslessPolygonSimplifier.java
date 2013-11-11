package com.hazardalert;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hazardalert.common.Assert;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/*
 * http://gis.stackexchange.com/questions/75551/lossless-polygon-simplification
 */
public class LosslessPolygonSimplifier {
	protected final static Logger logger = Logger.getLogger(LosslessPolygonSimplifier.class.getName());

	public static Polygon simplify(Polygon input) {
		final double AREA_THRESHOLD = 0.005; // allow excesses up to half a percent of total original area
		final double LINE_THRESHOLD = 0.0001; // fine threshold to strip straight lines
		try {
			if (!input.isSimple()) {
				logger.warning("Attempting to simplify complex polygon!");
			}
			Polygon simple = simplifyInternal(input, AREA_THRESHOLD, LINE_THRESHOLD);
			return simple;
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Failed to simplify. Resorting to convex hull.\n " + input.toText(), e);
			try {
				// worst case scenario - fall back to convex hull
				// probably a result of a bow-tie LINESTRING that doubles back on itself due to precision loss?
				return (Polygon) input.convexHull();
			}
			catch (Exception e2) {
				// Is this even possible? Polygons that cross the anti-meridian?
				logger.log(Level.SEVERE, "Failed to simplify to convex hull: " + input.toText(), e2);
				return input; // Garbage In, Garbage Out
			}
		}
	}

	// TODO avoid creating triangles on long straight edges
	public static Polygon simplifyInternal(Polygon original, double areaThreshold, double lineThreshold) {
		GeometryFactory gf = new GeometryFactory();
		Geometry excesses, excess, keepTotal, keepA, keepB, chA, chB, keep = null, elim = null;
		// pre-strip straight lines to avoid pathological time case
		original = (Polygon) com.vividsolutions.jts.simplify.TopologyPreservingSimplifier.simplify(original, lineThreshold);
		Polygon simplified = null, wrapper = (Polygon) original.convexHull();
		try {
			boolean done = false;
			while (!done) {
				done = true;
				excesses = wrapper.difference(original);
				for (int i = 0; i < excesses.getNumGeometries(); i++) {
					excess = excesses.getGeometryN(i);
					if (excess.getArea() / original.getArea() > areaThreshold) {
						done = false; // excess too big - try to split then shrink
						keepTotal = excess.intersection(original);
						keepA = gf.createGeometryCollection(null);
						keepB = gf.createGeometryCollection(null);
						for (int j = 0; j < keepTotal.getNumGeometries(); j++) {
							if (j < keepTotal.getNumGeometries() / 2) {
								keepA = keepA.union(keepTotal.getGeometryN(j));
							}
							else {
								keepB = keepB.union(keepTotal.getGeometryN(j));
							}
						}
						chA = keepA.convexHull();
						chB = keepB.convexHull();
						keep = gf.createMultiPolygon(null);
						if (chA instanceof Polygon) {
							keep = keep.union(chA);
						}
						if (chB instanceof Polygon) {
							keep = keep.union(chB);
						}
						elim = excess.difference(keep);
						wrapper = (Polygon) wrapper.difference(elim);
					}
				}
			}
			new Assert(wrapper.getArea() >= original.getArea());
			new Assert(wrapper.getArea() <= original.convexHull().getArea());
			simplified = (Polygon) com.vividsolutions.jts.simplify.TopologyPreservingSimplifier.simplify(wrapper, lineThreshold);
			new Assert(simplified.getNumPoints() <= original.getNumPoints());
			new Assert(simplified.getNumInteriorRing() == 0);
			new Assert(simplified.isSimple());
			return simplified;
		}
		catch (Exception e) {
			if (original.isSimple()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Failed to simplify non-complex polygon!");
				sb.append("\noriginal: " + original.toText());
				sb.append("\nwrapper: " + (null == wrapper ? "" : wrapper.toText()));
				sb.append("\nsimplified: " + (null == simplified ? "" : simplified.toText()));
				sb.append("\nkeep: " + (null == keep ? "" : keep.toText()));
				sb.append("\nelim: " + (null == elim ? "" : elim.toText()));
				logger.log(Level.SEVERE, sb.toString());
			}
			throw e;
		}
	}
}
