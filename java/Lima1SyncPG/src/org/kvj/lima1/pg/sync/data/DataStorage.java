package org.kvj.lima1.pg.sync.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static JSONObject getData(DataSource ds, String app, String user,
			String token, long from, boolean inc) {
		Connection c = null;
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			int slots = schema.optInt("_slots", 10);
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			PreparedStatement data = c
					.prepareStatement("select "
							+ "token, stream, object, status, updated, object_id "
							+ "from data "
							+ "where user_id=? and app=? and updated>? order by updated");
			data.setLong(1, userID);
			data.setString(2, app);
			data.setLong(3, from);
			ResultSet set = data.executeQuery();
			JSONArray arr = new JSONArray();
			int slots_used = 0;
			while (set.next()) {
				// log.info("About to send entity: {}", dataEntity);
				String _token = set.getString(1);
				if (inc) {
					if (token.equals(_token)) {
						// log.info("Skip own token {}", from);
						continue;
					}
				}
				String _stream = set.getString(2);
				JSONObject config = schema.getJSONObject(_stream);
				if (null == config) {
					log.error("Not found config for stream {}", _stream);
					continue;
				}
				int slots_needed = config.optInt("out", 1);
				if (slots_used + slots_needed > slots) {
					// log.info("Reached number of slots: {}", slots_used);
					break;
				}
				slots_used += slots_needed;
				JSONObject dataObject = new JSONObject();
				dataObject.put("s", _stream);
				dataObject.put("o", set.getString(3));
				dataObject.put("st", set.getInt(4));
				dataObject.put("u", set.getLong(5));
				dataObject.put("i", set.getLong(6));
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
		} finally {
			DAO.closeConnection(c);
		}
		return null;
	}

	public static String saveData(DataSource ds, JSONArray data, String app,
			String user, String token) {
		Connection c = null;
		try {
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			PreparedStatement findData = c
					.prepareStatement("select id from data where user_id=? and app=? and object_id=?");
			findData.setLong(1, userID);
			findData.setString(2, app);
			PreparedStatement insertData = c
					.prepareStatement("insert into data "
							+ "(user_id, app, token, id, object_id, stream, object, status, updated) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			insertData.setLong(1, userID);
			insertData.setString(2, app);
			insertData.setString(3, token);
			PreparedStatement updateData = c
					.prepareStatement("update data set "
							+ "token=?, object=?, status=?, updated=? where id=?");
			updateData.setString(1, token);
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				long objectID = item.getLong("i");
				findData.setLong(3, objectID);
				ResultSet findDataSet = findData.executeQuery();

				if (!findDataSet.next()) {
					// New data entry
					insertData.setLong(4, DAO.nextID(c));
					insertData.setLong(5, objectID);
					insertData.setString(6, item.getString("s"));
					insertData.setString(7, item.getString("o"));
					insertData.setInt(8, item.getInt("st"));
					insertData.setLong(9, nextUpdated());
					insertData.execute();
				} else {
					updateData.setString(2, item.getString("o"));
					updateData.setInt(3, item.getInt("st"));
					updateData.setLong(4, nextUpdated());
					updateData.setLong(5, findDataSet.getLong(1));
					updateData.execute();
				}
			}
			if (data.length() > 0) {
				ChannelStorage.dataUpdated(app, user, token);
			}
			return null;
		} catch (Exception e) {
			log.error("Save error", e);
			return "Database error";
		} finally {
			DAO.closeConnection(c);
		}
	}

	static class FKey {
		List<String> fks = new ArrayList<String>();
		List<String> data = new ArrayList<String>();
	}

	static final int MAX_DATA_SIZE = 100000;

	public static int backupData(DataSource ds, String app, String user,
			ZipOutputStream out) throws Exception {
		Connection c = null;
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			int filesAdded = 0;
			int dataSize = MAX_DATA_SIZE;
			ZipEntry zipEntry = null;
			// List<Integer> statuses = new ArrayList<Integer>();
			// statuses.add(0);
			// statuses.add(1);
			// statuses.add(2);
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
			@SuppressWarnings("unchecked")
			Iterator<String> keys = schema.keys();
			PreparedStatement data = c
					.prepareStatement("select object "
							+ "from data "
							+ "where user_id=? and app=? and status in (0, 1, 2) and stream=? "
							+ "order by object_id");
			data.setLong(1, userID);
			data.setString(2, app);
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
				data.setString(3, key);
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
				ResultSet dataSet = data.executeQuery();
				while (dataSet.next()) {
					JSONObject object = null;
					try {
						object = new JSONObject(dataSet.getString(1));
					} catch (Exception e) {
						log.warn("Not a JSON: " + dataSet.getString(1));
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
				log.debug("Stream " + key + " done. OK: " + entriesOK
						+ ". SKIP: " + entriesSkip);
			}
			if (null != zipEntry) {
				out.closeEntry();
			}
			log.debug("Backup done");
			return filesAdded;
		} catch (Exception e) {
			log.error("Backup error", e);
			throw e;
		} finally {
			DAO.closeConnection(c);
		}
	}

	public static String restoreData(DataSource ds, String app, String user,
			FileItem fileItem) {
		List<String> dataFiles = new ArrayList<String>();
		Connection c = null;
		try {
			List<String> allFiles = RestoreManager.getFilesInZip(fileItem
					.getInputStream());
			for (String file : allFiles) {
				if (file.startsWith("data") && file.endsWith(".json")) {
					dataFiles.add(file);
				}
			}
			if (0 == dataFiles.size()) {
				return "No valid files in " + fileItem.getName();
			}
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			PreparedStatement drop = c
					.prepareStatement("delete from tokens where user_id=?");
			drop.setLong(1, userID);
			drop.execute();
			drop = c.prepareStatement("delete from data where user_id=? and app=?");
			drop.setLong(1, userID);
			drop.setString(2, app);
			drop.execute();
			FileStorage.clearCache(c, app, user);
			PreparedStatement insertData = c
					.prepareStatement("insert into data "
							+ "(user_id, app, token, id, object_id, stream, object, status, updated) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			insertData.setLong(1, userID);
			insertData.setString(2, app);
			insertData.setString(3, "");
			String stream = null;
			Collections.sort(dataFiles);
			for (String file : dataFiles) {
				InputStream zip = RestoreManager.openFileInZip(
						fileItem.getInputStream(), file);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						zip, "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					if (line.startsWith("#")) {
						stream = line.substring(1);
						if (!schema.has(stream)) {
							log.warn("Skipping stream {}, no schema", stream);
							stream = null;
						}
						continue;
					}
					if (null == stream) {
						log.warn("Stream is empty, skipping");
						continue;
					}
					try {
						JSONObject obj = new JSONObject(line);
						if (!obj.has("id")) {
							log.warn("Invalid JSON, no ID {}", obj);
							continue;
						}
						insertData.setLong(4, DAO.nextID(c));
						insertData.setLong(5, obj.getLong("id"));
						insertData.setString(6, stream);
						insertData.setString(7, obj.toString());
						insertData.setInt(8, 1);
						insertData.setLong(9, nextUpdated());
						insertData.execute();
					} catch (Exception e) {
						log.warn("Error reading JSON", e);
						continue;
					}
				}
				br.close();
			}
			return null;
		} catch (Exception e) {
			log.error("Error restoring data", e);
		} finally {
			DAO.closeConnection(c);
		}
		return "Restore data error";
	}
}
