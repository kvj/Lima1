package org.kvj.lima1.pg.sync.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.common.OAuth;
import org.kvj.lima1.pg.sync.data.DAO;
import org.kvj.lima1.pg.sync.data.FileStorage;

public class DownloadServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String app = req.getParameter("app");
		String name = req.getParameter("name");
		log.info("Getting file: " + name);
		File file = FileStorage.getFile(app,
				(String) req.getAttribute(OAuth.OAUTH_CLIENT_ID), name);
		if (null == file) {
			throw new ServletException("File not found");
		} else {
			resp.setHeader("Content-Type", "application/octet-stream");
			String width = req.getParameter("width");
			if (null != width) {
				boolean done = FileStorage.resizeImageFile(file,
						Integer.parseInt(width), resp.getOutputStream());
				if (done) {
					resp.flushBuffer();
					return;
				}
			}
			FileInputStream stream = new FileInputStream(file);
			DAO.copyStream(stream, resp.getOutputStream());
			stream.close();
			resp.flushBuffer();
		}
	}
}
