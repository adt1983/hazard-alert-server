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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * This class is neither persistent nor thread safe.
 */
public final class DB {
	private static final Logger logger = Logger.getLogger(DB.class.getName());

	private DB() {
		throw new UnsupportedOperationException();
	}

	public static void pushAlert(Alert alert) {
		List<String> devices = alert.findIntersectingGCM();
		if (devices.isEmpty()) {
			logger.warning("No intersecting subscriptions.");
			return;
		}
		Queue queue = QueueFactory.getQueue("gcm");
		// send a multicast message using JSON
		// must split in chunks of 1000 devices (GCM limit)
		int total = devices.size();
		List<String> partialDevices = new ArrayList<String>(total);
		int counter = 0;
		int tasks = 0;
		for (String device : devices) {
			counter++;
			partialDevices.add(device);
			int partialSize = partialDevices.size();
			if (partialSize == MulticastMessage.MAX_MULTICAST_SIZE || counter == total) {
				Message message = alert.buildTickle();
				MulticastMessage msg = new MulticastMessage(message, partialDevices);
				msg.save();
				logger.info("Queuing " + partialSize + " devices on multicast " + msg.getId());
				TaskOptions taskOptions = SendMessageServlet.buildTask(msg);
				queue.add(taskOptions);
				partialDevices.clear();
				tasks++;
			}
		}
		logger.info("Queued tasks to send " + tasks + " multicast messages to " + total + " devices");
	}
}
