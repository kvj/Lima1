/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.amber.oauth2.rsfilter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.error.OAuthError;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.OAuthResponse;
import org.apache.amber.oauth2.common.message.types.ParameterStyle;
import org.apache.amber.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.amber.oauth2.rs.response.OAuthRSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maciej Machulak (m.p.machulak@ncl.ac.uk)
 * @author Lukasz Moren (lukasz.moren@ncl.ac.uk)
 * @author Aad van Moorsel (aad.vanmoorsel@ncl.ac.uk)
 */
public class OAuthFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(getClass());
	public static final String OAUTH_RS_PROVIDER_CLASS = "oauth.rs.provider-class";

	public static final String RS_REALM = "oauth.rs.realm";
	public static final String RS_REALM_DEFAULT = "OAuth Protected Service";

	public static final String RS_TOKENS = "oauth.rs.tokens";
	public static final ParameterStyle RS_TOKENS_DEFAULT = ParameterStyle.HEADER;

	private static final String TOKEN_DELIMITER = ",";

	private String realm;

	private OAuthRSProvider provider;

	private ParameterStyle[] parameterStyles;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		provider = OAuthUtils.initiateServletContext(
				filterConfig.getServletContext(), OAUTH_RS_PROVIDER_CLASS,
				OAuthRSProvider.class);
		realm = filterConfig.getServletContext().getInitParameter(RS_REALM);
		if (OAuthUtils.isEmpty(realm)) {
			realm = RS_REALM_DEFAULT;
		}

		String parameterStylesString = filterConfig.getServletContext()
				.getInitParameter(RS_TOKENS);
		if (OAuthUtils.isEmpty(parameterStylesString)) {
			parameterStyles = new ParameterStyle[] { RS_TOKENS_DEFAULT };
		} else {
			String[] parameters = parameterStylesString.split(TOKEN_DELIMITER);
			if (parameters != null && parameters.length > 0) {
				parameterStyles = new ParameterStyle[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					ParameterStyle tempParameterStyle = ParameterStyle
							.valueOf(parameters[i]);
					if (tempParameterStyle != null) {
						parameterStyles[i] = tempParameterStyle;
					} else {
						throw new ServletException("Incorrect ParameterStyle: "
								+ parameters[i]);
					}
				}
			}
		}

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		try {

			log.debug("Filtering {}", req.getRequestURI());
			// Make an OAuth Request out of this servlet request
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(
					req, parameterStyles);

			// Get the access token
			String accessToken = oauthRequest.getAccessToken();
			log.debug("Filtering token: {}", accessToken);

			final OAuthDecision decision = provider.validateRequest(realm,
					accessToken, req);
			if (!decision.isAuthorized()) {
				OAuthResponse oauthResponse = OAuthRSResponse
						.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setRealm(realm).setError("Invalid token")
						.setErrorDescription("Please authorize")
						.buildHeaderMessage();
				res.addHeader(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse
						.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
				res.sendError(oauthResponse.getResponseStatus());
				return;
			}
			final Principal principal = decision.getPrincipal();

			request = new HttpServletRequestWrapper(
					(HttpServletRequest) request) {
				@Override
				public String getRemoteUser() {
					return principal != null ? principal.getName() : null;
				}

				@Override
				public Principal getUserPrincipal() {
					return principal;
				}

			};

			request.setAttribute(OAuth.OAUTH_CLIENT_ID, decision
					.getOAuthClient().getClientId());
			request.setAttribute(OAuth.OAUTH_TOKEN, accessToken);

			chain.doFilter(request, response);
			return;

		} catch (OAuthSystemException e1) {
			throw new ServletException(e1);
		} catch (OAuthProblemException e) {
			log.error("OAuth exception", e);
			respondWithError(res, e);
			return;
		}

	}

	@Override
	public void destroy() {

	}

	private void respondWithError(HttpServletResponse resp,
			OAuthProblemException error) throws IOException, ServletException {

		OAuthResponse oauthResponse = null;

		try {
			if (OAuthUtils.isEmpty(error.getError())) {
				oauthResponse = OAuthRSResponse
						.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setRealm(realm).buildHeaderMessage();

			} else {

				int responseCode = 401;
				if (error.getError().equals(
						OAuthError.CodeResponse.INVALID_REQUEST)) {
					responseCode = 400;
				} else if (error.getError().equals(
						OAuthError.ResourceResponse.INSUFFICIENT_SCOPE)) {
					responseCode = 403;
				}

				oauthResponse = OAuthRSResponse.errorResponse(responseCode)
						.setRealm(realm).setError(error.getError())
						.setErrorDescription(error.getDescription())
						.setErrorUri(error.getUri()).buildHeaderMessage();
			}
			resp.addHeader(OAuth.HeaderType.WWW_AUTHENTICATE,
					oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
			resp.sendError(oauthResponse.getResponseStatus());
		} catch (OAuthSystemException e) {
			throw new ServletException(e);
		}
	}
}
