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
import org.kvj.lima1.pg.sync.data.FileStorage;

public class UploadServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String name = req.getParameter("name");
		String app = req.getParameter("app");
		FileItem item = null;
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(req);
			for (FileItem fileItem : items) {
				if (!fileItem.isFormField()) {
					item = fileItem;
					break;
				}
			}

		} catch (Exception e) {
			log.error("Error parsing upload request", e);
			throw new ServletException("Invalid request");
		}
		log.debug("Upload: " + name + " - " + item);
		if (null != name && null != item) {
			String result = FileStorage.upload(
					DAO.getDataSource(getServletContext()), app,
					(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID),
					item.getInputStream(), name);
			if (null != result) {
				throw new ServletException(result);
			}
			writeJSON(new JSONObject(), resp);
		} else {
			throw new ServletException("Invalid parameters");
		}
	}

	// @Override
	// protected JSONObject get(HttpServletRequest req) throws Exception {
	// JSONObject result = new JSONObject();
	// result.put(
	// "u",
	// blobstoreService.createUploadUrl("/rest/file/upload?name="
	// + req.getParameter("name") + "&oauth_token="
	// + req.getAttribute(OAuth.OAUTH_TOKEN) + "&app="
	// + req.getParameter("app")));
	// return result;
	// }
}
