package org.kvj.lima1.pg.sync.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.amber.oauth2.common.OAuth;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.pg.sync.data.SchemaStorage;

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
		}
		return schema;
	}
}
