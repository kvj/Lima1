package org.kvj.lima1.gae.sync;
import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Lima1SyncServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
