package com.hazardalert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

@Embeddable
public class Area {
	/*  TODO Can't create a SPATIAL INDEX on an InnoDB table
	 *  Break Area out into a separate MyISAM table? Can't have a FK span InnoDB/MyISAM
	 *  so we would need to maintain relationships at application layer. 
	 *  This is annoying.
	 */
	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(nullable = false)
	private Geometry area; // switch to MultiPolygon?

	public Geometry getArea() {
		return area;
	}

	public void setArea(Geometry area) {
		this.area = area;
		updateBounds();
	}

	public Area() {
		// Required for Hibernate
	}

	public Area(Geometry _area) {
		setArea(_area);
	}

	@Index(name = "maxLat")
	@Column(nullable = false)
	private Double maxLat;

	@Index(name = "maxLng")
	@Column(nullable = false)
	private Double maxLng;

	@Index(name = "minLat")
	@Column(nullable = false)
	private Double minLat;

	@Index(name = "minLng")
	@Column(nullable = false)
	private Double minLng;

	@PrePersist
	public void onPrePersist() {
		updateBounds();
	}

	private void updateBounds() {
		Envelope env = area.getEnvelopeInternal();
		this.maxLat = env.getMaxX();
		this.maxLng = env.getMaxY();
		this.minLat = env.getMinX();
		this.minLng = env.getMinY();
	}
}
