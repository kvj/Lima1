package org.kvj.lima1.sync.controller.net;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kvj.lima1.sync.controller.net.NetTransport.NetTransportException;
import org.kvj.lima1.sync.controller.net.NetTransport.RequestType;

import android.text.TextUtils;

public class OAuthProvider {

	public static interface OAuthProviderListener {

		public void onNeedToken();
	}

	private NetTransport transport;
	private String appID;
	private String token;
	private OAuthProviderListener listener;

	public OAuthProvider(NetTransport transport, String appID, String token, OAuthProviderListener listener) {
		this.transport = transport;
		this.appID = appID;
		this.token = token;
		this.listener = listener;
	}

	public JSONObject rest(String app, String path, Object body) throws NetTransportException {
		try {
			String uri = String.format("%sapp=%s&oauth_token=%s", path, app, token);
			return transport.request(uri, null == body ? RequestType.Get : RequestType.Post, body, null == body ? null
					: "text/plain; charset=utf-8");
		} catch (NetTransportException e) {
			if (e.getCode() == 401) {
				listener.onNeedToken();
			}
			throw e;
		}
	}

	public InputStream raw(String app, String path, Object body) throws NetTransportException {
		try {
			String uri = String.format("%sapp=%s&oauth_token=%s", path, app, token);
			return transport.rawRequest(uri, null == body ? RequestType.Get : RequestType.Post, body,
					null == body ? null : "text/plain; charset=utf-8");
		} catch (NetTransportException e) {
			if (e.getCode() == 401) {
				listener.onNeedToken();
			}
			throw e;
		}
	}

	public String tokenByUsernamePassword(String username, String password) throws NetTransportException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		params.put("client_id", appID);
		params.put("grant_type", "password");
		JSONObject json = transport.request("/token", RequestType.Post, params, null);
		String t = json.optString("access_token", "");
		if (!TextUtils.isEmpty(t)) {
			token = t;
			return t;
		}
		throw new NetTransportException(401, "No token provided", null);
	}
}
