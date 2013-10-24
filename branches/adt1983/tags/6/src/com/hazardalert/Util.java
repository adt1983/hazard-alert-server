package com.hazardalert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Util {
	private static final Logger logger = Logger.getLogger(Util.class.getName());

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private Util() {}

	public final static String loadUrl(String capUrl) throws IOException {
		URL url = new URL(capUrl);
		//https://groups.google.com/forum/?fromgroups=#!topic/cap-library-discuss/_CAUKbb8n48
		final int FIVE_SECONDS = 5 * 1000;
		final int SIXTY_SECONDS = 60 * 1000;
		URLConnection c = url.openConnection();
		if (c.getConnectTimeout() > FIVE_SECONDS) {
			logger.warning("Connect Timeout: " + c.getConnectTimeout());
		}
		else {
			c.setConnectTimeout(FIVE_SECONDS);
		}
		if (c.getReadTimeout() > SIXTY_SECONDS) {
			logger.warning("Read Timeout: " + c.getConnectTimeout());
		}
		else {
			c.setConnectTimeout(SIXTY_SECONDS);
		}
		return readFully(c.getInputStream());
	}

	public final static String readFully(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, UTF8));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		stream.close();
		return sb.toString();
	}

	public static <T> List<T> returnList(List<T> results) {
		if (null == results) {
			results = new LinkedList<T>();
		}
		return results;
	}
}
