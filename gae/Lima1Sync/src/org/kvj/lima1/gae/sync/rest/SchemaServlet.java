package org.kvj.lima1.gae.sync.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.amber.oauth2.common.OAuth;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.gae.sync.data.ChannelStorage;
import org.kvj.lima1.gae.sync.data.SchemaStorage;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class SchemaServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject get(HttpServletRequest req) throws Exception {
		String app = req.getParameter("app");
		log.info("Getting schema for {}", app);

		JSONObject schema = SchemaStorage.getInstance().getSchema(app);
		if (null == schema) {
			log.error("Schema not found for {}", app);
			throw new Exception("Schema not found");
		}
		String channel = req.getParameter("channel");
		if ("get".equals(channel)) {
			String userName = (String) req.getAttribute(OAuth.OAUTH_CLIENT_ID);
			String token = (String) req.getAttribute(OAuth.OAUTH_TOKEN);
			ChannelService channelService = ChannelServiceFactory
					.getChannelService();
			schema.put("_channel", channelService.createChannel(channel));
			ChannelStorage.newChannel(token, userName, channel);
		}
		return schema;
	}
}
