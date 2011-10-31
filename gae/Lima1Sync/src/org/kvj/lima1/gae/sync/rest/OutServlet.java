package org.kvj.lima1.gae.sync.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.amber.oauth2.common.OAuth;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.gae.sync.data.DataStorage;

public class OutServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	
	@Override
	protected JSONObject get(HttpServletRequest req) throws Exception {
		String from = req.getParameter("from");
		long fromLong = Long.parseLong(from);
		JSONObject result = DataStorage.getData(
				req.getParameter("app"), 
				(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID), 
				(String) req.getAttribute(OAuth.OAUTH_TOKEN), 
				fromLong);
		if (null == result) {
			throw new Exception("Error getting data");
		}
		return result;
	}
}
