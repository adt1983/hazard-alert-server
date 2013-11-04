package com.hazardalert;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.PostLoad;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

import com.google.android.gcm.server.Message;
import com.google.api.server.spi.config.ApiTransformer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.publicalerts.cap.Info;
import com.hazardalert.common.AlertFilter;
import com.hazardalert.common.Assert;
import com.hazardalert.common.Bounds;
import com.hazardalert.common.CommonUtil;
import com.vividsolutions.jts.geom.Geometry;

@ApiTransformer(AlertTransformer.class)
@Entity
public class Alert {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	@Index(name = "fullName")
	@Column(nullable = false)
	private String fullName; // extended message identifier in form "<sender>,<identifier>,<sent>"

	@Index(name = "expires")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expires")
	private Date expires; // expiration of last Hazard

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "effective")
	private Date effective;

	@ManyToOne
	private Alert updatedBy;

	@Embedded
	private Area area;

	@Transient
	private com.google.publicalerts.cap.Alert impl;

	@Lob
	@Column(length = 65536, nullable = false)
	private byte[] implData; // force a MEDIUMBLOB

	public Alert() {
		// Required for Hibernate
	}

	public Alert(com.google.publicalerts.cap.Alert _impl) {
		this.impl = _impl;
		this.implData = impl.toByteArray();
		setFullName(getFullName(impl));
		setExpires(new Date());
		setEffective(DatatypeConverter.parseDateTime(impl.getSent()).getTime()); // TODO ???
		area = new Area(CommonUtil.cap_to_jts(impl));
		for (Info i : impl.getInfoList()) {
			assert i.hasLanguage();
			assert i.hasEffective();
			assert i.hasExpires();
			Date infoExpires = DatatypeConverter.parseDateTime(i.getExpires()).getTime();
			if (infoExpires.after(expires)) {
				setExpires(infoExpires);
			}
		}
	}

	@PostLoad
	protected void onPostLoad() throws InvalidProtocolBufferException {
		impl = com.google.publicalerts.cap.Alert.parseFrom(implData);
	}

	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	private void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Date getEffective() {
		return effective;
	}

	public void setEffective(Date effective) {
		this.effective = effective;
	}

	public Alert getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Alert updatedBy) {
		this.updatedBy = updatedBy;
	}

	public com.google.publicalerts.cap.Alert getAlert() {
		return impl;
	}

	public Geometry getArea() {
		return area.getArea();
	}

	public <T> List<T> returnList(List<T> results) {
		if (null == results) {
			results = new LinkedList<T>();
		}
		return results;
	}

	public static List<Alert> search(AlertFilter filter) {
		String hql = "FROM Alert WHERE 1 = 1";
		if (null != filter.getInclude()) {
			hql += " AND intersects(area.area, GeomFromText(:include)) = true";
			hql += " AND NOT (area.maxLng < :minLng) AND NOT (area.maxLat < :minLat) AND NOT (area.minLng > :maxLng) AND NOT (area.minLat > :maxLat)";
		}
		if (null != filter.getExclude()) {
			hql += " AND NOT intersects(area.area, GeomFromText(:exclude)) = true";
		}
		/*if (null != filter.getMinExpires()) {
			sql = sql.concat(" AND expires > ?");
		}*/
		/*if (null != filter.getLimit()) {
			hql += " LIMIT ?";
		}*/
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			TypedQuery<Alert> q = em.createQuery(hql, Alert.class);
			if (null != filter.getInclude()) {
				Bounds include = filter.getInclude();
				q.setParameter("include", include.toPolygon().toText());
				q.setParameter("maxLng", include.getNe_lng());
				q.setParameter("maxLat", include.getNe_lat());
				q.setParameter("minLng", include.getSw_lng());
				q.setParameter("minLat", include.getSw_lat());
			}
			if (null != filter.getExclude()) {
				q.setParameter("exclude", filter.getExclude().toPolygon().toText());
			}
			/*if (null != filter.getMinExpires()) {
				ps.setDate(pi++, new java.sql.Date(filter.getMinExpires()));
			}*/
			if (null != filter.getLimit()) {
				//q.setParameter(pi++, filter.getLimit().longValue());
				q.setMaxResults((int) filter.getLimit().longValue());
			}
			return Util.returnList(q.getResultList());
		}
		finally {
			em.close();
		}
	}

	public List<String> findIntersectingGCM() {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			List<String> results = Util.returnList(em.createQuery(	"SELECT s.gcm FROM Subscription s, Alert a WHERE intersects(a.area.area, s.area.area) = true AND a.id = :id",
																	String.class)
														.setParameter("id", getId())
														.getResultList());
			if (0 == results.size()) {
				// check that we commited the alert and that it can at least find itself
				new Assert(Util.returnList(em.createQuery(	"FROM Alert a WHERE intersects(a.area.area, a.area.area) = true AND a.id = :id",
															Alert.class)
												.setParameter("id", getId())
												.getResultList())
								.size() == 1);
			}
			return results;
		}
		finally {
			em.close();
		}
	}

	public static void deleteExpired() {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Alert WHERE expires < current_timestamp()").executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}

	public int pushTTL() {
		return (int) Math.max(0, ((expires.getTime() - new Date().getTime()) / 1000));
	}

	public Message buildTickle() {
		return new Message.Builder().timeToLive(pushTTL()).addData("fullName", getFullName()).build();
	}

	public static Alert get(Long id) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			return em.find(Alert.class, id);
		}
		finally {
			em.close();
		}
	}

	public static Alert find(String fullName) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			try {
				return em.createQuery("FROM Alert WHERE fullName = :fullName", Alert.class)
							.setParameter("fullName", fullName)
							.getSingleResult();
			}
			catch (NoResultException e) {
				return null;
			}
		}
		finally {
			em.close();
		}
	}

	public static String getFullName(com.google.publicalerts.cap.Alert alert) {
		return alert.getSender() + "," + alert.getIdentifier() + "," + alert.getSent();
	}
}
