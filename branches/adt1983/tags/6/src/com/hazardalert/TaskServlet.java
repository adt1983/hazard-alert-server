package com.hazardalert;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class TaskServlet extends BaseServlet {
	private static final String HEADER_QUEUE_COUNT = "X-AppEngine-TaskRetryCount";
	private static final String HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
	private static final int MAX_RETRY = 3;

	/**
	 * Indicates to App Engine that this task should be retried.
	 */
	protected void setStatusRetry(HttpServletResponse resp) {
		resp.setStatus(500);
	}

	/**
	 * Indicates to App Engine that this task is done.
	 */
	protected void setStatusDone(HttpServletResponse resp) {
		resp.setStatus(200);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (req.getHeader(HEADER_QUEUE_NAME) == null) {
			throw new IOException("Missing header " + HEADER_QUEUE_NAME);
		}
		String retryCountHeader = req.getHeader(HEADER_QUEUE_COUNT);
		logger.fine("retry count: " + retryCountHeader);
		if (retryCountHeader != null) {
			int retryCount = Integer.parseInt(retryCountHeader);
			if (retryCount > MAX_RETRY) {
				logger.severe("Too many retries, dropping task. " + "retryCount: " + retryCount);
				setStatusDone(resp);
				return;
			}
		}
	}
}
