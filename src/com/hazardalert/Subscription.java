package com.hazardalert;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

import com.hazardalert.common.Bounds;
import com.hazardalert.common.CommonUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@Entity
public class Subscription {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Index(name = "gcm")
	@Column(nullable = false)
	private String gcm;

	@Index(name = "expires")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expires")
	private Date expires; // expiration of last Hazard

	@Embedded
	private Area area;

	public Subscription() {
		// Required for Hibernate
	}

	public Subscription(String gcm, Coordinate northeast, Coordinate southwest) {
		this.gcm = gcm;
		this.area.setArea(CommonUtil.createBoundingBox(northeast, southwest));
	}

	public Subscription(String gcm, Geometry area, Long expires) {
		this.gcm = gcm;
		this.area = new Area(area);
		this.expires = new Date(expires.longValue());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGcm() {
		return gcm;
	}

	public void setGcm(String gcm) {
		this.gcm = gcm;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Area _getArea() {
		return area;
	}

	public void _setArea(Area area) {
		this.area = area;
	}

	public Bounds getBounds() {
		return new Bounds(_getArea().getArea().getEnvelopeInternal());
	}

	public void setBounds(Bounds bounds) {
		_getArea().setArea(bounds.toPolygon());
	}

	public static Subscription get(Long id) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			return em.find(Subscription.class, id);
		}
		finally {
			em.close();
		}
	}

	public static void deleteExpired() {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Subscription WHERE expires < current_timestamp()").executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}

	public static void delete(String gcm) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Subscription WHERE gcm = :gcm").setParameter("gcm", gcm).executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}

	public static void updateGCM(String oldGCM, String newGCM) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("UPDATE Subscription SET gcm = :newGCM WHERE gcm = :oldGCM")
				.setParameter("oldGCM", oldGCM)
				.setParameter("newGCM", newGCM)
				.executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}
}