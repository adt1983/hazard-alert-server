package com.hazardalert;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.publicalerts.cap.feed.CapFeedParser;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

//TODO metrics for alert.sent
@SuppressWarnings("serial")
public class ReceiveAlertServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final CapFeedParser parser;

	public ReceiveAlertServlet() {
		this.parser = new CapFeedParser(true);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(req.getParameter("hub.challenge"));
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		Alert.deleteExpired();
		Subscription.deleteExpired();
		//TODO: it would be nice to have the full payload of req.getReader() for logging purposes but it can only be read once.
		// Extracting its full contents to a String causes exceptions b/c it gets truncated on large feeds.
		String postData = "";
		try {
			Queue queue = QueueFactory.getQueue("ingest");
			postData = U.readFully(req.getInputStream());
			//SyndFeed feed = parser.parseFeed(req.getReader());
			SyndFeed feed = parser.parseFeed(postData);
			@SuppressWarnings("unchecked") List<SyndEntry> entries = feed.getEntries();
			for (SyndEntry se : entries) {
				String capUrl = "";
				try {
					capUrl = parser.getCapUrl(se);
					if (null == capUrl) {
						// not all entries have a CAP URL, Low Magnitude Earthquakes
						// for example
						logger.warning("No CAP URL found.");
						continue;
					}
					logger.info("CAP URL: " + capUrl);
					TaskOptions task = IngestAlertServlet.buildTask(capUrl);
					queue.add(task);
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Error while processing: " + capUrl, e);
					// give up on this entry but continue trying any remaining entries
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Feed Error: \n" + postData, e);
			// don't rethrow the exception so Alert Hub thinks the post was successful. we don't want to be spammed by retries that are just going to fail all over again
		}
	}
}