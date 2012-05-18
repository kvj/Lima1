package org.kvj.lima1.pg.sync;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.kvj.lima1.pg.sync.data.DAO;

public class DBConnectionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		DAO.initServet(config);
	}

	@Override
	public void destroy() {
		super.destroy();
		DAO.destroyServlet(this);
	}

}
