package org.kvj.lima1.sync.controller.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.net.Proxy;
import android.util.Log;

public class HttpClientTransport implements NetTransport {

	private static final String TAG = "Http";
	private String url = "";
	private String proxyHost = null;
	private int proxyPort = 0;

	@Override
	public void setURL(Context context, String url) {
		this.url = url;
		proxyHost = Proxy.getHost(context);
		proxyPort = Proxy.getPort(context);
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
				UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject request(String uri, RequestType type, Object data, String contentType)
			throws NetTransportException {
		int code = 500;
		try {
			Log.i(TAG, "Request: " + uri + ", " + type);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", sf, 443));
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
			httpClient.setParams(params);

			if (proxyHost != null && proxyPort > 0) {
				params.setParameter(ConnRouteParams.DEFAULT_PROXY, new HttpHost(proxyHost, proxyPort));
			}
			DefaultHttpClient nHttpClient = new DefaultHttpClient(cm, params);
			HttpUriRequest request = new HttpGet(url + uri);
			if (null != data && type != RequestType.Get) {
				HttpPost post = new HttpPost(url + uri);
				if (data instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) data;
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					for (String key : map.keySet()) {
						pairs.add(new BasicNameValuePair(key, map.get(key).toString()));
					}
					post.setEntity(new UrlEncodedFormEntity(pairs));
				}
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					StringEntity entity = new StringEntity(json.toString(), "utf-8");
					if (null != contentType) {
						entity.setContentType(contentType);
					}
					post.setEntity(entity);
				}
				request = post;
			}
			HttpResponse response = nHttpClient.execute(request);
			code = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				JSONObject result = new JSONObject(readString(entity));
				if (200 == code) {
					return result;
				}
				Log.i(TAG, "JSON: " + result);
				throw new NetTransportException(code, result.optString("message",
						result.optString("error_description", "No error message")), null);
			}
			throw new NetTransportException(code, "No response", null);
		} catch (NetTransportException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetTransportException(code, e.getMessage(), e);
		}
	}

	private String readString(HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException,
			IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(entity.getContent(), "utf-8");
		int ch = -1;
		while ((ch = reader.read()) != -1) {
			sb.append((char) ch);
		}
		reader.close();
		return sb.toString();
	}
}
