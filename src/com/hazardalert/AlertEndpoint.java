package com.hazardalert;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.hazardalert.common.AlertFilter;
import com.hazardalert.common.Bounds;
import com.vividsolutions.jts.geom.Geometry;

@Api(name = "alertendpoint")
public class AlertEndpoint {
	protected final Logger logger = Logger.getLogger(getClass().getName());

	@ApiMethod(name = "alert.get")
	public Alert getAlert(@Named("id") Long id) {
		StringBuffer log = new StringBuffer();
		try {
			return Alert.get(id);
		}
		catch (RuntimeException e) {
			log.append("alertId: " + id + "\n");
			logger.severe(log.toString());
			throw e;
		}
	}

	@ApiMethod(name = "alert.find")
	public Alert alertFind(@Nullable @Named("fullName") String fullName) {
		logger.info(fullName);
		return Alert.find(fullName);
	}

	@ApiMethod(name = "alert.list", httpMethod = "POST")
	public List<Alert> listAlert(AlertFilter filter) {
		try {
			return Alert.search(filter);
		}
		catch (RuntimeException e) {
			logger.severe(filter.toString());
			throw e;
		}
	}

	@ApiMethod(name = "sender.list")
	public List<Sender> senderList() {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			return U.toNonNull(em.createQuery("FROM Sender", Sender.class).getResultList());
		}
		finally {
			em.close();
		}
	}

	@ApiMethod(name = "subscription.create")
	public Subscription createSubscription(@Named("gcm") String gcm, Bounds bounds, @Named("expires") Long expires) {
		Geometry area = bounds.toPolygon();
		Subscription s = new Subscription(gcm, area, expires);
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(s);
			em.getTransaction().commit();
			return s;
		}
		finally {
			em.close();
		}
	}

	@ApiMethod(name = "subscription.get")
	public Subscription subscriptionGet(@Named("id") long id) {
		return Subscription.get(id);
	}

	@ApiMethod(name = "alert.updateSubscription")
	public List<Alert> updateSubscription(@Named("id") Long id, @Named("gcm") String gcm, Bounds bounds) {
		Subscription s = Subscription.get(id);
		Bounds oldBounds = s.getBounds();
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			s._getArea().setArea(bounds.toPolygon());
			em.merge(s);
			em.getTransaction().commit();
			return Alert.search(new AlertFilter().setInclude(bounds).setExclude(oldBounds));
		}
		finally {
			em.close();
		}
	}

	@ApiMethod(name = "subscription.updateExpires", httpMethod = "POST")
	public Subscription subscriptionUpdateExpires(@Named("id") Long id, @Named("expires") Long expires) {
		Subscription s = Subscription.get(id);
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			em.getTransaction().begin();
			s.setExpires(new Date(expires));
			em.merge(s);
			em.getTransaction().commit();
			return s;
		}
		finally {
			em.close();
		}
	}
}
