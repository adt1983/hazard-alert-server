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
import com.google.publicalerts.cap.CapException;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.NotCapException;
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

	public String entryToString(SyndEntry entry) {
		return "Title: " + entry.getTitle() + //
				"\nLink:" + entry.getLink() + //
				"\nDescription: " + entry.getDescription() == null ? "" : entry.getDescription().getValue();
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
			postData = U.readFully(req.getInputStream()); // TODO: Move out of main try/catch to retry on IOException?
			//SyndFeed feed = parser.parseFeed(req.getReader());
			SyndFeed feed = parser.parseFeed(postData);
			@SuppressWarnings("unchecked") List<SyndEntry> entries = feed.getEntries();
			for (SyndEntry entry : entries) {
				String capUrl = "";
				try {
					capUrl = parser.getCapUrl(entry);
					if (null != capUrl) {
						logger.info("CAP URL: " + capUrl);
						TaskOptions task = IngestAlertServlet.buildTask(capUrl);
						queue.add(task);
						continue;
					}
					com.google.publicalerts.cap.Alert alert = parser.parseAlert(entry);
					TaskOptions task = IngestAlertServlet.buildTaskXml(new CapXmlBuilder().toXml(alert));
					queue.add(task);
				}
				catch (NotCapException nce) {
					// not all entries have a CAP URL, Low Magnitude Earthquakes for example
					logger.log(Level.WARNING, "Entry Warning: \n" + entryToString(entry), nce);
				}
				catch (CapException ce) {
					logger.log(Level.SEVERE, "Entry Error: \n" + entryToString(entry), ce);
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