package org.kvj.lima1.pg.sync;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.as.issuer.MD5Generator;
import org.apache.amber.oauth2.as.issuer.OAuthIssuer;
import org.apache.amber.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.amber.oauth2.as.request.OAuthAuthzRequest;
import org.apache.amber.oauth2.as.request.OAuthRequest;
import org.apache.amber.oauth2.as.response.OAuthASResponse;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.message.OAuthResponse;
import org.apache.amber.oauth2.common.message.types.ResponseType;
import org.apache.amber.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthServlet extends HttpServlet {

	OAuthIssuer oauthIssuer = null;
	private Logger log = LoggerFactory.getLogger(OAuthAuthServlet.class);
	
	public OAuthAuthServlet() {
		oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
	}
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			try {
				OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
				// some code ....
				String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
				OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse
					.authorizationResponse(HttpServletResponse.SC_FOUND);
				if (responseType.equals(ResponseType.CODE.toString()) || responseType
		                .equals(ResponseType.CODE_AND_TOKEN.toString())) {
		                builder.setCode(oauthIssuer.authorizationCode());
		        }
				if (responseType.equals(ResponseType.TOKEN.toString()) || responseType
		                .equals(ResponseType.CODE_AND_TOKEN.toString())) {
		                builder.setAccessToken(oauthIssuer.accessToken());
		                builder.setExpiresIn(String.valueOf(3600));
		        }
				String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
				// build OAuth response
				OAuthResponse resp = builder.location(redirectURI).buildQueryMessage();

				response.sendRedirect(resp.getLocationUri());

				// if something goes wrong
			} catch (OAuthProblemException ex) {
				String redirectUri = ex.getRedirectUri();
				if (OAuthUtils.isEmpty(redirectUri)) {
	                throw new Exception("No redirect URI");
	            }
				final OAuthResponse resp = OAuthASResponse
						.errorResponse(HttpServletResponse.SC_FOUND).error(ex)
						.location(redirectUri).buildQueryMessage();

				response.sendRedirect(resp.getLocationUri());
			}
			super.doGet(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}
