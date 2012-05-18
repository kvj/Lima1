package org.kvj.lima1.pg.sync.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.common.OAuth;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.pg.sync.data.DAO;
import org.kvj.lima1.pg.sync.data.FileStorage;

public class RemoveFileServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String name = req.getParameter("name");
		String app = req.getParameter("app");
		if (null != name) {
			String result = FileStorage.removeFile(
					DAO.getDataSource(getServletContext()), app,
					(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID), name);
			if (null != result) {
				throw new ServletException(result);
			}
			writeJSON(new JSONObject(), resp);
		} else {
			throw new ServletException("Invalid parameters");
		}
	}

}
