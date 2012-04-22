package org.kvj.lima1.gae.sync.rest;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.common.OAuth;
import org.kvj.lima1.gae.sync.data.DataStorage;
import org.kvj.lima1.gae.sync.data.FileStorage;

public class BackupServlet extends OAuthSecuredServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String app = req.getParameter("app");
		String fileName = req.getParameter("fname");
		String type = req.getParameter("type");
		if (null == type) {
			type = "data";
		}
		if (null == fileName) {
			fileName = "backup-" + app + ".zip";
		}
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");
		try {
			String user = (String) req.getAttribute(OAuth.OAUTH_CLIENT_ID);
			ZipOutputStream zip = new ZipOutputStream(resp.getOutputStream());
			if ("data".equals(type)) {
				ZipEntry entry = new ZipEntry("data.json");
				zip.putNextEntry(entry);
				DataStorage.backupData(app, user, zip);
				zip.closeEntry();
			}
			if ("file".equals(type)) {
				String from = req.getParameter("from");
				long fromLong = 0;
				if (null != from) {
					fromLong = Long.parseLong(from);
				}
				int filesAdded = FileStorage.backupFiles(app, user, fromLong,
						zip);
				if (filesAdded == 0) {
					ZipEntry noFiles = new ZipEntry(".no-files");
					zip.putNextEntry(noFiles);
					zip.closeEntry();
				}
			}
			zip.close();
			resp.flushBuffer();
		} catch (Exception e) {
			log.error("Error in backup", e);
			throw new ServletException(e);
		}
	}
}
