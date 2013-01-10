package org.kvj.lima1.sync.controller.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SchemaInfo {
	private static final String TAG = "SchemaInfo";
	public Map<String, TableInfo> tables = new HashMap<String, TableInfo>();
	int revision = 0;

	private void parseTableData(JSONObject data, TableInfo info) throws JSONException {
		JSONArray texts = data.optJSONArray("texts");
		if (null != texts) { // Have texts
			for (int i = 0; i < texts.length(); i++) { // Copy
				info.texts.add(texts.getString(i));
			}
		}
		JSONArray numbers = data.optJSONArray("numbers");
		if (null != numbers) { // Have numbers
			for (int i = 0; i < numbers.length(); i++) { // Copy
				info.numbers.add(numbers.getString(i));
			}
		}
		JSONArray indexes = data.optJSONArray("indexes");
		if (null != indexes) { // Have indexes
			for (int i = 0; i < indexes.length(); i++) { // Copy
				JSONArray index = indexes.getJSONArray(i);
				List<String> oneIndex = new ArrayList<String>();
				for (int j = 0; j < index.length(); j++) { // Copy
					oneIndex.add(index.getString(j));
				}
				if (!oneIndex.isEmpty()) { // Have index
					info.indexes.add(oneIndex);
				}
			}
		}
		// Log.i(TAG, "parseTableData: " + info.texts + ", " + info.numbers +
		// ", " + info.indexes);
	}

	private int parseSchema(JSONObject schema, Map<String, TableInfo> infos) throws JSONException {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = schema.keys();
		while (keys.hasNext()) { //
			String key = keys.next();
			if (!key.startsWith("_")) { // Not a reserved word
				JSONObject table = schema.getJSONObject(key);
				TableInfo tinfo = infos.get(key);
				if (null == tinfo) { //
					tinfo = new TableInfo();
					infos.put(key, tinfo);
				}
				parseTableData(table, tinfo);
			}
		}
		if (schema.has("_fkeys")) { // Have foreign keys
			JSONArray fks = schema.getJSONArray("_fkeys");
			for (int i = 0; i < fks.length(); i++) { // Process
				JSONObject fkey = fks.getJSONObject(i);
				String[] pk = fkey.getString("pk").split("\\.");
				String[] fk = fkey.getString("fk").split("\\.");
				TableInfo pkInfo = infos.get(pk[0]);
				TableInfo fkInfo = infos.get(fk[0]);
				if (null != pkInfo && null != fkInfo) { // Both exists
					FKey key = new FKey();
					key.table = fk[0];
					key.field = fk[1];
					pkInfo.fkeys.add(key);
				}
			}
		}
		if (schema.has("_upgrades")) { // Have upgrades
			JSONArray upgrades = schema.getJSONArray("_upgrades");
			for (int i = 0; i < upgrades.length(); i++) { // Upgrade schema
				parseSchema(upgrades.getJSONObject(i), infos);
			}
			return upgrades.length();
		}
		return 0;
	}

	void parseSchema(JSONObject schema) throws JSONException {
		tables.clear();
		Map<String, TableInfo> infos = new HashMap<String, TableInfo>();
		parseSchema(schema, infos);
		tables.putAll(infos);
		revision = schema.optInt("_rev");
		// Log.i(TAG, "parseSchema: " + tables);
	}

	@Override
	public String toString() {
		return "SchemaInfo: " + revision + ": " + tables.keySet().size();
	}
}
