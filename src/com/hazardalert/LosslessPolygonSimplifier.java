package com.hazardalert;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hazardalert.common.Assert;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Polygonal;

/*
 * TODO we are adding back in a bunch of "polygonlets" - worried this going to be pointless on large jagged polygons
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
		Geometry excesses, excess, g, firstHalf, secondHalf, ch1, ch2, keep = null, elim = null;
		Polygon simplified = null, wrapper = (Polygon) original.convexHull();
		try {
			boolean done = false;
			while (!done) {
				done = true;
				excesses = wrapper.difference(original);
				for (int i = 0; i < excesses.getNumGeometries(); i++) {
					excess = excesses.getGeometryN(i);
					if (excess.getArea() / original.getArea() > areaThreshold) {
						done = false; // too big - try to split then shrink
						g = excess.intersection(original);
						firstHalf = gf.createGeometryCollection(null);
						secondHalf = gf.createGeometryCollection(null);
						for (int j = 0; j < g.getNumGeometries(); j++) {
							if (j < g.getNumGeometries() / 2) {
								firstHalf = firstHalf.union(g.getGeometryN(j));
							}
							else {
								secondHalf = secondHalf.union(g.getGeometryN(j));
							}
						}
						ch1 = firstHalf.convexHull();
						ch2 = secondHalf.convexHull();
						keep = gf.createMultiPolygon(null);
						if (ch1 instanceof Polygonal) {
							keep = keep.union(ch1);
						}
						if (ch2 instanceof Polygonal) {
							keep = keep.union(ch2);
						}
						elim = excess.difference(keep);
						wrapper = (Polygon) wrapper.difference(elim);
						break;
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
	/*
	private static Polygon eliminateBowTies(Polygon input) {
		if (input.isSimple()) {
			return input;
		}
		GeometryFactory gf = new GeometryFactory();
		Coordinate in[] = input.getCoordinates();
		Coordinate points[] = new Coordinate[4];
		for (int i = 0 ; i < in.length ; i++) {
			points[0] = in[i + 0];
			points[1] = in[i + 1];
			points[2] = in[i + 2];
			points[3] = in[i + 3];
			LineString ls = gf.createLineString(points);
		}
	}*/
}
