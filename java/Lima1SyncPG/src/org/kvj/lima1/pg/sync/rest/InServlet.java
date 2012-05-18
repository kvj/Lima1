package org.kvj.lima1.pg.sync.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.amber.oauth2.common.OAuth;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.pg.sync.data.DAO;
import org.kvj.lima1.pg.sync.data.DataStorage;

public class InServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject post(JSONObject in, HttpServletRequest req)
			throws Exception {
		// log.info("Saving: {}", in);
		String error = DataStorage.saveData(
				DAO.getDataSource(getServletContext()), in.optJSONArray("a"),
				req.getParameter("app"),
				(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID),
				(String) req.getAttribute(OAuth.OAUTH_TOKEN));
		if (null != error) {
			log.error("Error saving data: {}", error);
			throw new Exception(error);
		}
		return null;
	}

}
