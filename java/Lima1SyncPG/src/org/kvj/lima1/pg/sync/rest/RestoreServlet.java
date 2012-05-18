package org.kvj.lima1.pg.sync.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.common.OAuth;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jettison.json.JSONObject;
import org.kvj.lima1.pg.sync.data.DAO;
import org.kvj.lima1.pg.sync.data.RestoreManager;

public class RestoreServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String app = req.getParameter("app");
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(req);
			log.debug("Restore: " + app + " - " + items.size());

			String result = RestoreManager.restoreFromFiles(
					DAO.getDataSource(getServletContext()), app,
					(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID), items);
			if (null != result) {
				throw new ServletException(result);
			}
			writeJSON(new JSONObject(), resp);
		} catch (Exception e) {
			log.error("Error parsing upload request", e);
			throw new ServletException("Invalid request");
		}
	}
}
