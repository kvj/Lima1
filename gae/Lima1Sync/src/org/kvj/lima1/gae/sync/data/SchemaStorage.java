package org.kvj.lima1.gae.sync.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaStorage {

	private static final String SCHEMA_FILE = "/schema.json";

	private static SchemaStorage instance = null;

	private Map<String, JSONObject> schemas = new HashMap<String, JSONObject>();
	private Logger log = LoggerFactory.getLogger(getClass());

	private SchemaStorage() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream(SCHEMA_FILE), "utf-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			JSONObject object = new JSONObject(builder.toString());
			for (@SuppressWarnings("unchecked")
			Iterator<String> it = object.keys(); it.hasNext();) {
				String key = it.next();
				log.info("Loaded schema for app: {}", key);
				schemas.put(key, object.getJSONObject(key));
			}
		} catch (Exception e) {
			log.error("Error loading schema storage", e);
		}
	}

	public static SchemaStorage getInstance() {
		if (null == instance) {
			instance = new SchemaStorage();
		}
		return instance;
	}

	public JSONObject getSchema(String app) {
		log.info("Getting app schema: {}", app);
		return schemas.get(app);
	}
}
