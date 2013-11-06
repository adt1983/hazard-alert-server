package com.hazardalert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;

import org.hibernate.annotations.Index;

@Entity
public class Sender {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Index(name = "sender")
	@Column(nullable = false)
	private String sender;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false, columnDefinition = "boolean default false")
	private Boolean suppress; // Client side hint for default value - not used by server

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getSuppress() {
		return suppress;
	}

	public void setSuppress(Boolean suppress) {
		this.suppress = suppress;
	}

	public static Sender find(String sender) {
		EntityManager em = ApiKeyInitializer.createEntityManager();
		try {
			try {
				return em.createQuery("FROM Sender WHERE sender = :sender", Sender.class).setParameter("sender", sender).getSingleResult();
			}
			catch (NoResultException e) {
				return null;
			}
		}
		finally {
			em.close();
		}
	}
}
