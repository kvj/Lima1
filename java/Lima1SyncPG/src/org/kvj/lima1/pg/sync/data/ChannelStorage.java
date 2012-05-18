package org.kvj.lima1.pg.sync.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelStorage {

	private static Logger log = LoggerFactory.getLogger(ChannelStorage.class);

	private static class ChannelInfo {
		String userName = null;
		String token = null;
		String channel = null;
		String app = null;
	}

	private static final Map<String, ChannelInfo> channels = new HashMap<String, ChannelInfo>();

	public static void newChannel(String app, String token, String userName,
			String channel) {
		ChannelInfo info = new ChannelInfo();
		info.userName = userName;
		info.token = token;
		info.channel = channel;
		info.app = app;
		synchronized (channels) {
			channels.put(token, info);
		}
	}

	public static void dataUpdated(String app, String user, String token) {
		synchronized (channels) {
			// log.info("Data updated: " + app + ", " + user + ", "
			// + channels.size());
			List<String> removes = new ArrayList<String>();
			for (String t : channels.keySet()) {
				ChannelInfo info = channels.get(t);
				// log.info("Check token: " + info.app + ", " + info.userName
				// + ", " + info.token + " = " + token);

				if (app.equals(info.app) && user.equals(info.userName)
						&& !info.token.equals(token)) {
					// Our case - notify
					try {
						// log.info("Found socket - send");
					} catch (Exception e) {
						log.warn("Token expired", e);
						removes.add(t);
					}
				}
			}
			for (String t : removes) {
				channels.remove(t);
			}
		}
	}
}
