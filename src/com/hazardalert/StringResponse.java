package com.hazardalert;

import javax.persistence.Entity;

@Entity
public class StringResponse {
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;

	public StringResponse(String s) {
		value = s;
	}
}
