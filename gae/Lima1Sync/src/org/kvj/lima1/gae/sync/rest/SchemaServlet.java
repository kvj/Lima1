package org.kvj.lima1.gae.sync.rest;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.gae.sync.data.SchemaStorage;

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
		return schema;
	}
}
