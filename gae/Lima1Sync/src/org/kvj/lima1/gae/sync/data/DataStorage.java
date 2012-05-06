package org.kvj.lima1.gae.sync.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

public class DataStorage {

	private static Logger log = LoggerFactory.getLogger(DataStorage.class);
	private static long nextID = 0;

	private static synchronized long nextUpdated() {
		long result = new Date().getTime();
		while (result <= nextID) {
			result++;
		}
		nextID = result;
		return result;
	}

	public static JSONObject getData(String app, String user, String token,
			long from, boolean inc) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			int slots = schema.optInt("_slots", 10);
			Query existing = new Query("User").addFilter("username",
					FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				throw new Exception("User not found");
			}
			Query data = new Query("Data")
					.addFilter("user", FilterOperator.EQUAL,
							userEntity.getKey())
					.addFilter("app", FilterOperator.EQUAL, app)
					.addFilter("updated", FilterOperator.GREATER_THAN, from)
					.addSort("updated");
			JSONArray arr = new JSONArray();
			int slots_used = 0;
			for (Entity dataEntity : datastore.prepare(data).asIterable()) {
				// log.info("About to send entity: {}", dataEntity);
				if (inc) {
					if (token.equals(dataEntity.getProperty("token"))) {
						// log.info("Skip own token {}", from);
						continue;
					}
				}
				String stream = (String) dataEntity.getProperty("stream");
				JSONObject config = schema.getJSONObject(stream);
				if (null == config) {
					log.error("Not found config for stream {}", stream);
					continue;
				}
				int slots_needed = config.optInt("out", 1);
				if (slots_used + slots_needed > slots) {
					// log.info("Reached number of slots: {}", slots_used);
					break;
				}
				slots_used += slots_needed;
				JSONObject dataObject = new JSONObject();
				Text oText = (Text) dataEntity.getProperty("object");
				dataObject.put("s", stream);
				dataObject.put("st", dataEntity.getProperty("status"));
				dataObject.put("u", dataEntity.getProperty("updated"));
				dataObject.put("i", dataEntity.getProperty("id"));
				dataObject.put("o", oText.getValue());
				arr.put(dataObject);
			}
			// log.info("Sending arr: {}", arr.length());
			JSONObject result = new JSONObject();
			result.put("a", arr);
			if (0 == arr.length()) {
				result.put("u", nextUpdated());
			}
			return result;

		} catch (Exception e) {
			log.error("Get data error", e);
		}
		return null;
	}

	public static String saveData(JSONArray data, String app, String user,
			String token) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			Query existing = new Query("User").addFilter("username",
					FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				return "User not found";
			}
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				Query existingData = new Query("Data").addFilter("user",
						FilterOperator.EQUAL, userEntity.getKey()).addFilter(
						"id", FilterOperator.EQUAL, item.getLong("i"));
				Entity dataEntity = datastore.prepare(existingData)
						.asSingleEntity();
				if (null == dataEntity) {
					dataEntity = new Entity("Data", userEntity.getKey());
					dataEntity.setProperty("id", item.getLong("i"));
					dataEntity.setProperty("app", app);
					dataEntity.setProperty("stream", item.getString("s"));
					dataEntity.setProperty("user", userEntity.getKey());
				}
				dataEntity.setProperty("object", new Text(item.getString("o")));
				dataEntity.setProperty("status", item.getInt("st"));
				dataEntity.setProperty("updated", nextUpdated());
				dataEntity.setProperty("token", token);
				datastore.put(txn, dataEntity);
				// log.info("Saved entity: {}", item);
			}
			txn.commit();
			if (data.length() > 0) {
				ChannelStorage.dataUpdated(app, user, token);
			}
			return null;
		} catch (Exception e) {
			log.error("Save error", e);
			return "Database error";
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	static class FKey {
		List<String> fks = new ArrayList<String>();
		List<String> data = new ArrayList<String>();
	}

	static final int MAX_DATA_SIZE = 100000;

	public static int backupData(String app, String user, ZipOutputStream out)
			throws Exception {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			Query existing = new Query("User").addFilter("username",
					FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				throw new Exception("User not found");
			}
			int filesAdded = 0;
			int dataSize = MAX_DATA_SIZE;
			ZipEntry zipEntry = null;
			List<Integer> statuses = new ArrayList<Integer>();
			statuses.add(0);
			statuses.add(1);
			statuses.add(2);
			Map<String, FKey> fkeys = new HashMap<String, DataStorage.FKey>();
			if (schema.has("_fkeys")) {
				JSONArray _fkeys = schema.getJSONArray("_fkeys");
				for (int i = 0; i < _fkeys.length(); i++) {
					JSONObject _fkey = _fkeys.getJSONObject(i);
					FKey fkey = fkeys.get(_fkey.optString("pk"));
					if (null == fkey) {
						fkey = new FKey();
						fkeys.put(_fkey.optString("pk"), fkey);
					}
					fkey.fks.add(_fkey.optString("fk"));
				}
			}
			log.info("Before backup fkeys:" + fkeys.size());
			Iterator<String> keys = schema.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key.startsWith("_")) {
					continue;
				}
				Map<String, FKey> fkeysToSave = new HashMap<String, DataStorage.FKey>();
				Map<String, FKey> fkeysToCheck = new HashMap<String, DataStorage.FKey>();
				for (String pk : fkeys.keySet()) {
					FKey fkey = fkeys.get(pk);
					if (pk.startsWith(key + ".")) {
						fkeysToSave.put(pk.substring(key.length() + 1), fkey);
					}
					for (String fk : fkey.fks) {
						if (fk.startsWith(key + ".")) {
							fkeysToCheck.put(fk.substring(key.length() + 1),
									fkey);
						}
					}
				}
				log.info("Stream " + key + " start. Check: "
						+ fkeysToCheck.size() + ". Save: " + fkeysToSave.size());
				long entriesOK = 0;
				long entriesSkip = 0;
				Query data = new Query("Data")
						.addFilter("user", FilterOperator.EQUAL,
								userEntity.getKey())
						.addFilter("app", FilterOperator.EQUAL, app)
						.addFilter("stream", FilterOperator.EQUAL, key)
						.addFilter("status", FilterOperator.IN, statuses)
						// .addFilter("updated", FilterOperator.GREATER_THAN,
						// from)
						.addSort("id");
				byte[] headerData = new String("#" + key + "\n")
						.getBytes("utf-8");
				if (null == zipEntry
						|| dataSize + headerData.length > MAX_DATA_SIZE) {
					if (null != zipEntry) {
						out.closeEntry();
					}
					zipEntry = new ZipEntry(String.format("data%03d.json",
							filesAdded));
					out.putNextEntry(zipEntry);
					filesAdded++;
					dataSize = 0;
				}
				out.write(headerData);
				dataSize += headerData.length;
				for (Entity dataEntity : datastore.prepare(data).asIterable()) {
					Text oText = (Text) dataEntity.getProperty("object");
					JSONObject object = null;
					try {
						object = new JSONObject(oText.getValue());
					} catch (Exception e) {
						log.warn("Not a JSON: " + oText.getValue());
						entriesSkip++;
						continue;
					}
					boolean putEntry = true;
					// Check foreign keys
					for (String field : fkeysToCheck.keySet()) {
						FKey fkey = fkeysToCheck.get(field);
						if (object.has(field)) {
							String value = object.optString(field);
							if (null != value && !fkey.data.contains(value)) {
								putEntry = false;
								break;
							}
						}
					}
					if (putEntry) {
						// Write entry
						entriesOK++;
						byte[] objectData = new String(object.toString() + "\n")
								.getBytes("utf-8");
						if (null == zipEntry
								|| dataSize + objectData.length > MAX_DATA_SIZE) {
							if (null != zipEntry) {
								out.closeEntry();
							}
							zipEntry = new ZipEntry(String.format(
									"data%03d.json", filesAdded));
							out.putNextEntry(zipEntry);
							filesAdded++;
							dataSize = 0;
						}
						out.write(objectData);
						dataSize += objectData.length;
						// Save primary keys, if have
						for (String field : fkeysToSave.keySet()) {
							FKey fkey = fkeysToSave.get(field);
							if (object.has(field)) {
								String value = object.optString(field);
								if (null != value) {
									fkey.data.add(value);
								}
							}
						}
					} else {
						entriesSkip++;
					}
				}
				out.flush();
				log.info("Stream " + key + " done. OK: " + entriesOK
						+ ". SKIP: " + entriesSkip);
			}
			if (null != zipEntry) {
				out.closeEntry();
			}
			log.info("Backup done");
			return filesAdded;
		} catch (Exception e) {
			log.error("Backup error", e);
			throw e;
		}
	}
}
