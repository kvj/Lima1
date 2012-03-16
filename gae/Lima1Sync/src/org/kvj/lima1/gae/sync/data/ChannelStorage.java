package org.kvj.lima1.gae.sync.data;

import java.util.HashMap;
import java.util.Map;

public class ChannelStorage {

	private static class ChannelInfo {
		String userName = null;
		String token = null;
		String channel = null;
	}

	private static final Map<String, ChannelInfo> channels = new HashMap<String, ChannelInfo>();

	public static void newChannel(String token, String userName, String channel) {
		ChannelInfo info = new ChannelInfo();
		info.userName = userName;
		info.token = token;
		info.channel = channel;
		channels.put(token, info);
	}
}
