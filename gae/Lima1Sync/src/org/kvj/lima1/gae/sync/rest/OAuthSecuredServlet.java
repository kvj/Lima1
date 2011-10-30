package org.kvj.lima1.gae.sync.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthSecuredServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	protected Logger log = LoggerFactory.getLogger(getClass());

	private void writeJSON(JSONObject object, ServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		writer.write(object.toString());
		writer.flush();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject object = get(req);
			writeJSON(object, resp);
		} catch (Exception e) {
			log.error("Error in REST", e);
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					req.getInputStream(), "utf-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			log.info("Parse POST body: {}", builder);
			JSONObject object = new JSONObject(builder.toString());
			JSONObject outObject = post(object, req);
			if (null == outObject) {
				outObject = new JSONObject();
			}
			writeJSON(outObject, resp);
		} catch (Exception e) {
			log.error("Error in REST", e);
			throw new ServletException(e);
		}
		
	}
	
	protected JSONObject get(HttpServletRequest req) throws Exception {
		throw new Exception("Not supported");
	}
	
	protected JSONObject post(JSONObject in, HttpServletRequest req) throws Exception {
		throw new Exception("Not supported");
	}
}
