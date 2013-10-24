package com.hazardalert;

import com.google.appengine.api.datastore.Text;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class Fips {
	@Id
	public String fips;

	public Text polygon;

	public Polygon toPolygon() {
		Polygon.Builder builder = Polygon.newBuilder();
		if (null == polygon.getValue()) {
			throw new RuntimeException();
		}
		String[] points = polygon.getValue().split(" ");
		if (points.length < 5) {
			throw new RuntimeException();
		}
		for (String p : points) {
			String[] values = p.split(",");
			if (2 != values.length) {
				throw new RuntimeException();
			}
			Point.Builder point = Point.newBuilder();
			point.setLatitude(Double.parseDouble(values[0]));
			point.setLongitude(Double.parseDouble(values[1]));
			builder.addPoint(point);
		}
		return builder.build();
	}
}
