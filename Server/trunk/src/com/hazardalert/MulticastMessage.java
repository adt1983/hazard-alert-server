package com.hazardalert;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.google.android.gcm.server.Message;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

@Entity
@Cache
public class MulticastMessage {
	@Id
	public Long id; // Long for auto-generate

	@Serialize
	public Message message;

	@Serialize
	public List<String> devices;

	static final int MAX_MULTICAST_SIZE = 1000;

	@SuppressWarnings("unused")
	private MulticastMessage() {}

	public MulticastMessage(List<String> deviceIdList) {
		message = new Message.Builder().build();
		devices = deviceIdList;
	}

	public MulticastMessage(Message msg, List<String> recipients) {
		message = msg;
		devices = recipients;
	}

	@Override
	public String toString() {
		return "MulticastMessage(id=" + id + ", devices.size=" + devices.size() + "message=" + message + ")";
	}

	public String getId() {
		return id.toString();
	}

	public void delete() {
		ofy().delete().type(MulticastMessage.class).id(id);
	}

	public static MulticastMessage get(String _id) {
		return ofy().load().type(MulticastMessage.class).id(new Long(_id)).get();
	}

	public static MulticastMessage safeGet(String key) {
		return ofy().load().type(MulticastMessage.class).id(new Long(key)).safeGet();
	}

	public void save() {
		ofy().save().entity(this).now();
	}
}