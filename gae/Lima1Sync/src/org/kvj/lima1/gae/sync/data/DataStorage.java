package org.kvj.lima1.gae.sync.data;

import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
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
					dataEntity = new Entity("Data");
					dataEntity.setProperty("id", item.getLong("i"));
					dataEntity.setProperty("stream", item.getString("s"));
					dataEntity.setProperty("user", userEntity.getKey());
				}
				dataEntity.setProperty("object", item.getString("o"));
				dataEntity.setProperty("status", item.getInt("st"));
				dataEntity.setProperty("updated", nextUpdated());
				dataEntity.setProperty("token", token);
				datastore.put(txn, dataEntity);
				log.info("Saved entity: {}", item);
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
}
