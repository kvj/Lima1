package org.kvj.lima1.gae.sync.data;

import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

public class DataStorage {

	private static Logger log = LoggerFactory.getLogger(DataStorage.class);
	private static long nextID = 0;
	
	private static synchronized long nextUpdated() {
		long result = new Date().getTime();
		while(result<=nextID) {
			result++;
		}
		nextID = result;
		return result;
	}
	
	public static JSONObject getData(String app, String user, String token, long from, boolean inc) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			int slots = schema.optInt("_slots", 10);
			Query existing = new Query("User").
					addFilter("username", FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				throw new Exception("User not found");
			}
			Query data = new Query("Data").
				addFilter("user", FilterOperator.EQUAL, userEntity.getKey()).
				addFilter("app", FilterOperator.EQUAL, app).
				addFilter("updated", FilterOperator.GREATER_THAN, from).
				addSort("updated");
			JSONArray arr = new JSONArray();
			int slots_used = 0;
			for (Entity dataEntity: datastore.prepare(data).asIterable()) {
//				log.info("About to send entity: {}", dataEntity);
				if (inc) {
					if (token.equals(dataEntity.getProperty("token"))) {
						log.info("Skip own token {}", from);
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
				if (slots_used+slots_needed>slots) {
					log.info("Reached number of slots: {}", slots_used);
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
			log.info("Sending arr: {}", arr.length());
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
	
	public static String saveData(JSONArray data, String app, String user, String token) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			Query existing = new Query("User").
					addFilter("username", FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				return "User not found";
			}
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				Query existingData = new Query("Data").
						addFilter("user", FilterOperator.EQUAL, userEntity.getKey()).
						addFilter("id", FilterOperator.EQUAL, item.getLong("i"));
				Entity dataEntity = datastore.prepare(existingData).asSingleEntity();
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
				log.info("Saved entity: {}", item);
			}
			txn.commit();
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
}
