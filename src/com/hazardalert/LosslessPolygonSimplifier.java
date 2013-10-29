package com.hazardalert;

import com.hazardalert.common.Assert;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/*
 * TODO we are adding back in a bunch of "polygonlets" - worried this going to be pointless on large jagged polygons
 * http://gis.stackexchange.com/questions/75551/lossless-polygon-simplification
 */
public class LosslessPolygonSimplifier {
	public static Polygon simplify(Polygon input) {
		Polygon simple = (Polygon) com.vividsolutions.jts.simplify.TopologyPreservingSimplifier.simplify(input, 0.01);
		simple = (Polygon) simple.union(input); // re-attach any polygons we 'oversimplified'
		simple = new Polygon((LinearRing) simple.getExteriorRing(), null, new GeometryFactory()); // drop any holes
		//Polygon convexHull = (Polygon) input.convexHull();
		Geometry diff = input.difference(simple);
		//new Assert(input.getArea() <= simple.getArea() && simple.getArea() <= convexHull.getArea());
		new Assert(0 == simple.getNumInteriorRing());
		//new Assert(convexHull.covers(input));
		//new Assert(convexHull.covers(simple));
		new Assert(diff.isEmpty() || (diff.getArea() / simple.getArea()) < 0.0001); // shouldn't .covers be enough?
		return simple;
	}
}
