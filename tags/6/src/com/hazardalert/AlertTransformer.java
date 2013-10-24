package com.hazardalert;

import com.google.api.server.spi.config.Transformer;
import com.hazardalert.common.AlertTransport;

public class AlertTransformer implements Transformer<Alert, AlertTransport> {
	@Override
	public Alert transformFrom(AlertTransport at) {
		// should never be creating an alert sent to us from the client?
		throw new RuntimeException();
	}

	@Override
	public AlertTransport transformTo(Alert a) {
		return new AlertTransport().setPayload(javax.xml.bind.DatatypeConverter.printBase64Binary(a.getAlert().toByteArray()));
	}
}
