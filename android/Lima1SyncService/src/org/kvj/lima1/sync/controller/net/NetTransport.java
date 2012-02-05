package org.kvj.lima1.sync.controller.net;

import org.json.JSONObject;

import android.content.Context;

public interface NetTransport {

	public static class NetTransportException extends Exception {

		private int code = 500;
		private Exception exception = null;

		public NetTransportException(int code, String message,
				Exception exception) {
			super(message);
			this.code = code;
			this.exception = exception;
		}

		public int getCode() {
			return code;
		}

		public Exception getException() {
			return exception;
		}
	}

	public enum RequestType {
		Get, Post, Put
	};

	public void setURL(Context context, String url);

	public JSONObject request(String uri, RequestType type, Object data,
			String contentType) throws NetTransportException;
}
