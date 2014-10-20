/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazardalert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/*
	<headline>Severe Weather Statement issued May 19 at 3:47PM CDT until May 19 at 4:15PM CDT by NWS Wichita</headline>
	<description>...A TORNADO WARNING REMAINS IN EFFECT FOR SOUTHERN SEDGWICK COUNTY
	UNTIL 415 PM CDT...
	...TORNADO EMERGENCY FOR WICHITA...
	AT 345 PM CDT...A CONFIRMED LARGE...VIOLENT AND EXTREMELY DANGEROUS
	TORNADO WAS LOCATED ON THE SOUTHWEST SIDE OF WICHITA...AND MOVING
	NORTHEAST AT 30 MPH.
	THIS IS A PARTICULARLY DANGEROUS SITUATION.
	HAZARD...DEADLY TORNADO.
	SOURCE...WEATHER SPOTTERS CONFIRMED TORNADO.
	IMPACT...YOU COULD BE KILLED IF NOT UNDERGROUND OR IN A TORNADO
	SHELTER. COMPLETE DESTRUCTION OF NEIGHBORHOODS...BUSINESSES
	AND VEHICLES WILL OCCUR. FLYING DEBRIS WILL BE DEADLY TO
	PEOPLE AND ANIMALS.
	LOCATIONS IMPACTED INCLUDE...
	MAIZE...DOWNTOWN WICHITA...WICHITA...BEL AIRE...MCCONNELL AIR FORC...
	EAST WICHITA AND OAKLAWN.
	...OBSERVED
	TORNADO DAMAGE THREAT...CATASTROPHIC
	HAIL...2.75IN</description>
	<instruction>THIS IS AN EXTREMELY DANGEROUS TORNADO WITH COMPLETE DEVASTATION
	LIKELY. YOU COULD BE KILLED IF NOT UNDERGROUND OR IN A TORNADO
	SHELTER. DO NOT DELAY...SEEK SHELTER NOW! IF NO UNDERGROUND SHELTER
	IS AVAILABLE SEEK SHELTER IN AN INTERIOR ROOM OF THE LOWEST LEVEL OF
	A STRUCTURE...OR IF TIME ALLOWS...CONSIDER MOVING TO AN UNDERGROUND
	SHELTER ELSEWHERE. MOBILE HOMES AND OUTBUILDINGS WILL OFFER NO
	SHELTER FROM THIS TORNADO.</instruction>

	- NOAA-NWS-ALERTS-KS124F0072435C.SevereWeatherStatement.124F00725DECKS.ICTSVSICT.aadc968127f502331a0afa733084b7b5
 */
/**
 * Servlet that sends a message to a device.
 * <p>
 * This servlet is invoked by AppEngine's Push Queue mechanism.
 */
//TODO: this control path is questionable
@SuppressWarnings("serial")
public class SendMessageServlet extends TaskServlet {
	static final String PARAMETER_MULTICAST = "multicastKey";

	private Sender sender;

	public static TaskOptions buildTask(MulticastMessage msg) {
		return TaskOptions.Builder.withUrl("/send").param(PARAMETER_MULTICAST, msg.getId()).method(Method.POST);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		sender = newSender(config);
	}

	/**
	 * Creates the {@link Sender} based on the servlet settings.
	 */
	protected Sender newSender(ServletConfig config) {
		String key = (String) config.getServletContext().getAttribute(ApiKeyInitializer.ATTRIBUTE_ACCESS_KEY);
		return new Sender(key);
	}

	/**
	 * Processes the request to add a new message.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		setStatusDone(resp);
		MulticastMessage msg = null;
		try {
			String multicastKey = req.getParameter(PARAMETER_MULTICAST);
			msg = MulticastMessage.safeGet(multicastKey);
			sendMulticastMessage(msg, resp);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception posting " + (msg == null ? "<null>" : msg.message), e);
			setStatusRetry(resp);
			super.doPost(req, resp); // don't retry if we are over the retry limit
		}
	}

	private void sendMulticastMessage(MulticastMessage msg, HttpServletResponse resp) {
		MulticastResult multicastResult;
		try {
			multicastResult = sender.sendNoRetry(msg.message, msg.devices);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		boolean allDone = true;
		// check if any registration id must be updated
		if (multicastResult.getCanonicalIds() != 0) {
			List<Result> results = multicastResult.getResults();
			for (int i = 0; i < results.size(); i++) {
				String canonicalRegId = results.get(i).getCanonicalRegistrationId();
				if (canonicalRegId != null) {
					String regId = msg.devices.get(i);
					Subscription.updateGCM(regId, canonicalRegId);
				}
			}
		}
		if (multicastResult.getFailure() != 0) {
			// there were failures, check if any could be retried
			List<Result> results = multicastResult.getResults();
			List<String> retriableRegIds = new ArrayList<String>();
			for (int i = 0; i < results.size(); i++) {
				String error = results.get(i).getErrorCodeName();
				if (error != null) {
					String regId = msg.devices.get(i);
					logger.warning("Got error (" + error + ") for regId " + regId);
					if (error.equals(Constants.ERROR_NOT_REGISTERED) || error.equals(Constants.ERROR_INVALID_REGISTRATION)) {
						// application has been removed from device - unregister
						// it
						/*TODO
						 * Need client detection/recovery code to re-register. GCM 3.0 can help us out?
						 * Example: APA91bH9ryMTFuozhmcdfADOGg0olRuGbmG6BYgJYYmoCtBzGr6eC0oVDEYw6vsI5T_92ugda7i-yn_NNi-5TmtYJwV_rSMczPWZrOeoYhAOr3K0TKRQuw22cYT-NbMELMGmBL_K61WQumGbkrelhTSgkCFD4cAOjA
						 */
						Subscription.delete(regId);
					}
					else if (error.equals(Constants.ERROR_UNAVAILABLE) || error.equals(Constants.ERROR_QUOTA_EXCEEDED)
							|| error.equals(Constants.ERROR_DEVICE_QUOTA_EXCEEDED) || error.equals(Constants.ERROR_INTERNAL_SERVER_ERROR)) {
						retriableRegIds.add(regId);
					}
				}
			}
			if (!retriableRegIds.isEmpty()) {
				// update task
				msg.devices = retriableRegIds;
				msg.save();
				allDone = false;
				setStatusRetry(resp);
			}
		}
		if (allDone) {
			multicastDone(resp, msg);
		}
		else {
			setStatusRetry(resp);
		}
	}

	private void multicastDone(HttpServletResponse resp, MulticastMessage msg) {
		logger.info("Sent Multicast " + msg.getId() + " to " + msg.devices.size() + " devices");
		msg.delete();
		setStatusDone(resp);
	}
}
