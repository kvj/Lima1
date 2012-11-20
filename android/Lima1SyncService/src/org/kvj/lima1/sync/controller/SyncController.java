package org.kvj.lima1.sync.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.sync.Lima1SyncApp;
import org.kvj.lima1.sync.LoginForm;
import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;
import org.kvj.lima1.sync.R;
import org.kvj.lima1.sync.controller.data.AppInfo;
import org.kvj.lima1.sync.controller.data.TableInfo;
import org.kvj.lima1.sync.controller.net.HttpClientTransport;
import org.kvj.lima1.sync.controller.net.NetTransport.NetTransportException;
import org.kvj.lima1.sync.controller.net.OAuthProvider;
import org.kvj.lima1.sync.controller.net.OAuthProvider.OAuthProviderListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class SyncController implements OAuthProviderListener {

	public static interface SyncControllerListener {

		public void syncStarted();

		public void syncCompleted(String error);
	}

	private static final int LOGIN_ID = 1;
	private static final String TAG = "Sync";
	private HttpClientTransport transport;
	private OAuthProvider net;
	private Map<String, AppInfo> infos = new HashMap<String, AppInfo>();
	private SyncControllerListener listener = null;

	public SyncController(Lima1SyncApp context) {
		this.transport = new HttpClientTransport();
		transport.setURL(context, "https://lima1-kvj.rhcloud.com");
		this.net = new OAuthProvider(transport, "lima1android", context.getStringPreference(R.string.token,
				R.string.tokenDefault), this);
	}

	@Override
	public void onNeedToken() {
		NotificationManager notificationManager = (NotificationManager) Lima1SyncApp.getInstance().getSystemService(
				Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_login, "Lima1", System.currentTimeMillis());
		PendingIntent intent = PendingIntent.getActivity(Lima1SyncApp.getInstance(), 0,
				new Intent(Lima1SyncApp.getInstance(), LoginForm.class), PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(Lima1SyncApp.getInstance(), "Lima1", "Login/password required", intent);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(LOGIN_ID, notification);
	}

	public String verifyToken(String username, String password) {
		try {
			Log.i(TAG, "Verify: " + username + ", " + password);
			String token = net.tokenByUsernamePassword(username, password);
			Lima1SyncApp.getInstance().setStringPreference(R.string.token, token);
			return null;
		} catch (NetTransportException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String sync(String app) {
		AppInfo info = getInfo(app);
		if (null == info.db) {
			return "DB error";
		}
		if (null != listener) {
			listener.syncStarted();
		}
		try {
			info.db.getDatabase().beginTransaction();
			JSONObject newSchema = net.rest(app, "/rest/schema?", null);
			Log.i(TAG, "Schema: " + newSchema);
			boolean fullSync = false;
			long inFrom = 0;
			long outFrom = 0;
			int itemSent = 0;
			int itemReceived = 0;
			Cursor c = info.db.getDatabase().query("updates", new String[] { "version_in", "version_out" }, null, null,
					null, null, "id desc", "1");
			if (c.moveToFirst()) {
				inFrom = c.getLong(0);
				outFrom = c.getLong(1);
			}
			c.close();
			if (null == info.schemaInfo) {
				// Save schema, reset DB
				fullSync = true;
				outFrom = 0;
			}
			String upgradeResult = info.upgradeSchema(newSchema);
			if (null != upgradeResult) { // Upgrade failed
				throw new Exception(upgradeResult);
			}
			// Send changes
			int slots = newSchema.optInt("_slots", 10);
			JSONArray result = new JSONArray();
			for (String stream : info.schemaInfo.tables.keySet()) {
				// For every table
				int slotsUsed = 0;
				c = info.db.getDatabase().query("t_" + stream, new String[] { "id", "data", "updated", "status" },
						"own=? and updated>?", new String[] { "1", Long.toString(inFrom) }, null, null, "updated");
				int slotsNeeded = newSchema.optJSONObject(stream).optInt("in", 1);
				if (c.moveToFirst()) {
					do {
						slotsUsed += slotsNeeded;
						JSONObject json = new JSONObject();
						if (slotsUsed > slots) {
							// Send
							json.put("a", result);
							net.rest(app, "/rest/in?", json);
							slotsUsed = 0;
							continue;
						}
						json.put("s", stream);
						json.put("st", c.getInt(3));
						json.put("u", c.getLong(2));
						json.put("o", c.getString(1));
						json.put("i", c.getLong(0));
						result.put(json);
						itemSent++;
						inFrom = c.getLong(2);
					} while (c.moveToNext());
				}
			}
			if (result.length() > 0) {
				JSONObject json = new JSONObject();
				json.put("a", result);
				net.rest(app, "/rest/in?", json);
			}
			c.close();
			// Receive data
			while (true) {
				String url = String.format("/rest/out?from=%d&%s", outFrom, fullSync ? "" : "inc=yes&");
				JSONObject res = net.rest(app, url, null);
				JSONArray arr = res.getJSONArray("a");
				if (arr.length() == 0) {
					outFrom = res.getLong("u");
					break;
				}
				for (int i = 0; i < arr.length(); i++) {
					JSONObject object = arr.getJSONObject(i);
					outFrom = object.getLong("u");
					JSONObject obj = new JSONObject(object.getString("o"));
					create(app, object.getString("s"), obj, object.getInt("st"), object.getLong("u"));
					itemReceived++;
				}
			}
			// Finish sync: insert updates, remove removed data
			ContentValues values = new ContentValues();
			values.put("id", info.id());
			values.put("version_in", inFrom);
			values.put("version_out", outFrom);
			info.db.getDatabase().insert("updates", null, values);
			for (String stream : info.schemaInfo.tables.keySet()) {
				info.db.getDatabase().delete("t_" + stream, "status=?", new String[] { "3" });
			}
			info.db.getDatabase().setTransactionSuccessful();
			info.setSchema(newSchema);
			Log.i(TAG, "Sync done: out: " + itemSent + ", in: " + itemReceived);
			if (null != listener) {
				listener.syncCompleted(null);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			if (null != listener) {
				listener.syncCompleted("Error in sync");
			}
			return e.getMessage();
		} finally {
			info.db.getDatabase().endTransaction();
		}
	}

	private Long create(String app, String stream, JSONObject obj, int status, Long updated) throws Exception {
		AppInfo info = getInfo(app);
		ContentValues values = new ContentValues();
		TableInfo tinfo = info.getTableInfo(stream);
		if (null == tinfo) {
			throw new Exception("Not synchronized");
		}
		if (!obj.has("id")) {
			obj.put("id", info.id());
		}
		values.put("id", obj.getLong("id"));
		values.put("status", status);
		values.put("data", obj.toString());
		if (null != updated) {
			values.put("updated", updated.longValue());
			values.put("own", 0);
		} else {
			values.put("updated", obj.getLong("id"));
			values.put("own", 1);
		}
		for (String field : tinfo.numbers) { // Add numbers
			values.put("f_" + field, obj.optLong(field));
		}
		for (String field : tinfo.texts) { // Add texts
			values.put("f_" + field, obj.optString(field));
		}
		Cursor c = info.db.getDatabase().query("t_" + stream, new String[] { "id" }, "id=?",
				new String[] { Long.toString(obj.getLong("id")) }, null, null, null);
		if (c.moveToFirst()) { // Found - update
			info.db.getDatabase().update("t_" + stream, values, "id=?",
					new String[] { Long.toString(obj.getLong("id")) });
		} else {
			info.db.getDatabase().insert("t_" + stream, null, values);
		}
		c.close();
		return obj.optLong("id");
	}

	private synchronized AppInfo getInfo(String app) {
		AppInfo info = infos.get(app);
		if (null == info) {
			info = new AppInfo(Lima1SyncApp.getInstance(), app);
			infos.put(app, info);
		}
		return info;
	}

	public PJSONObject createUpdate(String app, String stream, PJSONObject obj) {
		AppInfo info = getInfo(app);
		TableInfo tinfo = info.getTableInfo(stream);
		if (null == tinfo) {
			Log.e(TAG, "No DB");
			return null;
		}
		try {
			info.db.getDatabase().beginTransaction();
			create(app, stream, obj, 1, null);
			info.db.getDatabase().setTransactionSuccessful();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			info.db.getDatabase().endTransaction();
		}
	}

	public PJSONObject remove(String app, String stream, PJSONObject obj) {
		AppInfo info = getInfo(app);
		TableInfo tinfo = info.getTableInfo(stream);
		if (null == tinfo) {
			Log.e(TAG, "No DB");
			return null;
		}
		try {
			info.db.getDatabase().beginTransaction();
			ContentValues values = new ContentValues();
			values.put("status", 3);
			values.put("updated", info.id());
			values.put("own", 1);
			info.db.getDatabase().update("t_" + stream, values, "id=?", new String[] { obj.optString("id") });
			info.db.getDatabase().setTransactionSuccessful();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			info.db.getDatabase().endTransaction();
		}
	}

	private int jsonIndexOf(JSONArray arr, String value) throws JSONException {
		if (null == arr) {
			return -1;
		}
		for (int i = 0; i < arr.length(); i++) {
			if (arr.getString(i).equals(value)) {
				return i;
			}
		}
		return -1;
	}

	private String parseOrder(String order, String def, TableInfo tinfo) throws JSONException {
		if (TextUtils.isEmpty(order)) {
			return def;
		}
		StringBuilder buffer = new StringBuilder();
		String[] parts = order.split(",");
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim();
			String[] arr = part.split("\\s");
			String field = arr[0];
			if (tinfo.numbers.contains(field) || tinfo.texts.contains(field)) {
				field = "f_" + field;
			}
			Log.i(TAG, "parseOrder: " + part + ", " + field);
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(field);
			if (arr.length > 1) {
				buffer.append(" " + arr[1]);
			}
		}
		return buffer.toString();
	}

	private String arrayToQuery(QueryOperator[] arr, List<String> values, String orand, TableInfo info)
			throws JSONException {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0, fields = 0; i < arr.length; i++) {
			QueryOperator op = arr[i];
			String field = null;
			if (info.numbers.contains(op.getName())) {
				field = "f_" + op.getName();
			} else {
				if (info.texts.contains(op.getName())) {
					field = "f_" + op.getName();
				}
			}
			if ("id".equals(op.getName())) {
				field = "id";
			}
			if ("updated".equals(op.getName())) {
				field = "updated";
			}
			if (null != field) {
				if (fields++ > 0) {
					buffer.append(" " + orand + " ");
				}
				buffer.append(field);
				buffer.append(op.getOperator());
				buffer.append("?");
				values.add(op.getValue());
			}
		}
		return buffer.toString();
	}

	public PJSONObject[] query(String app, String stream, QueryOperator[] ops, String order, String limit) {
		AppInfo info = getInfo(app);
		if (null == info.db) {
			Log.e(TAG, "No DB: " + app);
			return null;
		}
		TableInfo tinfo = info.getTableInfo(stream);
		if (null == tinfo) {
			Log.e(TAG, "Unsupported stream: " + stream + "::" + app);
			return null;
		}
		try {
			List<String> values = new ArrayList<String>();
			values.add("3");
			String where = "status<>?";
			if (null != ops) {
				String cond = arrayToQuery(ops, values, "and", tinfo);
				if (!TextUtils.isEmpty(cond)) {
					where += " and (" + cond + ")";
				}
			}
			Log.i(TAG, "Query: " + stream + ", " + where + ", " + values);
			Cursor c = info.db.getDatabase().query("t_" + stream, new String[] { "data" }, where,
					values.toArray(new String[0]), null, null, parseOrder(order, "id", tinfo), limit);
			List<PJSONObject> result = new ArrayList<PJSONObject>();
			if (c.moveToFirst()) {
				do {
					result.add(new PJSONObject(c.getString(0)));
				} while (c.moveToNext());
			}
			return result.toArray(new PJSONObject[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setListener(SyncControllerListener listener) {
		this.listener = listener;
	}

}
