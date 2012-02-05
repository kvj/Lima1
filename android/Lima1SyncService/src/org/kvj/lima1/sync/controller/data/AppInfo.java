package org.kvj.lima1.sync.controller.data;

import org.json.JSONObject;
import org.kvj.bravo7.ApplicationContext;

import android.text.TextUtils;

public class AppInfo {
	public AppDBHelper db = null;
	public String name = null;
	private long id = 0;
	public JSONObject schema = null;
	private ApplicationContext context;

	public AppInfo(ApplicationContext context, String name) {
		this.name = name;
		this.context = context;
		db = new AppDBHelper(context, name + "-app.db");
		if (!db.open()) {
			db = null;
		}
		String json = context.getStringPreference(name + "-schema", "");
		if (!TextUtils.isEmpty(json)) {
			try {
				schema = new JSONObject(json);
			} catch (Exception e) {
			}
		}
	}

	public synchronized long id() {
		long now = System.currentTimeMillis();
		if (now > id) {
			id = now;
			return id;
		}
		return ++id;
	}

	public void setSchema(JSONObject newSchema) {
		schema = newSchema;
		context.setStringPreference(name + "-schema", schema.toString());
	}
}
