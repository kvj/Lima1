package org.kvj.lima1.pg.sync;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.amber.oauth2.as.issuer.MD5Generator;
import org.apache.amber.oauth2.as.issuer.OAuthIssuer;
import org.apache.amber.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.amber.oauth2.as.request.OAuthTokenRequest;
import org.apache.amber.oauth2.as.response.OAuthASResponse;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.error.OAuthError;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.message.OAuthResponse;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.kvj.lima1.pg.sync.data.DAO;
import org.kvj.lima1.pg.sync.data.UserStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthTokenServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(OAuthTokenServlet.class);

	private String checkUserNamePassword(DataSource ds,
			OAuthTokenRequest request, String token) {
		log.info("Checking " + request.getUsername() + " and "
				+ request.getPassword() + ", save: " + token);
		return UserStorage.authorizeUser(ds, request.getUsername(),
				request.getPassword(), token);
	}

	private void writeOAuthResponse(OAuthResponse r,
			HttpServletResponse response) throws IOException {
		response.setStatus(r.getResponseStatus());
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(r.getBody());
		pw.flush();
		pw.close();

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		OAuthTokenRequest oauthRequest = null;
		DataSource ds = DAO.getDataSource(getServletContext());
		try {
			OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(
					new MD5Generator());

			try {
				oauthRequest = new OAuthTokenRequest(request);
				String accessToken = oauthIssuerImpl.accessToken();
				if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(
						GrantType.PASSWORD.toString())) {
					String message = checkUserNamePassword(ds, oauthRequest,
							accessToken);
					if (null != message) {
						OAuthResponse r = OAuthASResponse
								.errorResponse(
										HttpServletResponse.SC_BAD_REQUEST)
								.setError(
										OAuthError.TokenResponse.INVALID_GRANT)
								.setErrorDescription(message)
								.buildJSONMessage();
						writeOAuthResponse(r, response);
						return;
					}
					OAuthResponse r = OAuthASResponse
							.tokenResponse(HttpServletResponse.SC_OK)
							.setAccessToken(accessToken).setExpiresIn("0")
							.buildJSONMessage();
					writeOAuthResponse(r, response);
					return;
				}
				OAuthResponse r = OAuthASResponse
						.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
						.setError(OAuthError.TokenResponse.INVALID_GRANT)
						.setErrorDescription("unsupported grant_type")
						.buildJSONMessage();
				writeOAuthResponse(r, response);
				return;

				// if something goes wrong
			} catch (OAuthProblemException ex) {

				OAuthResponse r = OAuthResponse.errorResponse(401).error(ex)
						.buildJSONMessage();

				response.setStatus(r.getResponseStatus());
				writeOAuthResponse(r, response);
				return;
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
