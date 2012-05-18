package org.kvj.lima1.gae.sync.data;

import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;

public class FileStorage {

	private static Logger log = LoggerFactory.getLogger(FileStorage.class);

	public static String upload(String app, String user, BlobKey file,
			String name) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			Query existing = new Query("User").addFilter("username",
					FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				return "User not found";
			}
			Entity fileEntity = new Entity("File", userEntity.getKey());
			fileEntity.setProperty("app", app);
			fileEntity.setProperty("user", userEntity.getKey());
			fileEntity.setProperty("file", file);
			fileEntity.setProperty("created", new Date().getTime());
			fileEntity.setProperty("name", name);
			datastore.put(txn, fileEntity);
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

	public static BlobKey getFile(String app, String user, String name) {
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
			Query file = new Query("File")
					.addFilter("user", FilterOperator.EQUAL,
							userEntity.getKey())
					.addFilter("app", FilterOperator.EQUAL, app)
					.addFilter("name", FilterOperator.EQUAL, name);
			Entity fileEntity = datastore.prepare(file).asSingleEntity();
			if (null == fileEntity) {
				throw new Exception("File not found");
			}
			return (BlobKey) fileEntity.getProperty("file");
		} catch (Exception e) {
			log.error("Download error", e);
			return null;
		}
	}

	public static String removeFile(String app, String user, String name) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			Query existing = new Query("User").addFilter("username",
					FilterOperator.EQUAL, user);
			Entity userEntity = datastore.prepare(existing).asSingleEntity();
			if (null == userEntity) {
				return "User not found";
			}
			Query file = new Query("File")
					.addFilter("user", FilterOperator.EQUAL,
							userEntity.getKey())
					.addFilter("app", FilterOperator.EQUAL, app)
					.addFilter("name", FilterOperator.EQUAL, name);
			Entity fileEntity = datastore.prepare(file).asSingleEntity();
			if (null == fileEntity) {
				return null;
			}
			BlobstoreService blobstoreService = BlobstoreServiceFactory
					.getBlobstoreService();
			blobstoreService.delete((BlobKey) fileEntity.getProperty("file"));
			datastore.delete(txn, fileEntity.getKey());
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

	public static int backupFiles(String app, String user, long from,
			ZipOutputStream zip) {
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
			Query file = new Query("File").addFilter("user",
					FilterOperator.EQUAL, userEntity.getKey()).addFilter("app",
					FilterOperator.EQUAL, app);
			if (from > 0) {
				file.addFilter("created", FilterOperator.GREATER_THAN, from);
			}
			int filesAdded = 0;
			StringBuilder meta = new StringBuilder();
			for (Entity dataEntity : datastore.prepare(file).asIterable()) {
				BlobKey blob = (BlobKey) dataEntity.getProperty("file");
				String name = (String) dataEntity.getProperty("name");
				try {
					BlobstoreInputStream stream = new BlobstoreInputStream(blob);
					ZipEntry entry = new ZipEntry(name);
					zip.putNextEntry(entry);
					byte[] buffer = new byte[BlobstoreService.MAX_BLOB_FETCH_SIZE];
					int bytesRead = 0;
					while ((bytesRead = stream.read(buffer)) > 0) {
						zip.write(buffer, 0, bytesRead);
					}
					stream.close();
					zip.closeEntry();
					filesAdded++;
					zip.flush();
					try {
						JSONObject metaObject = new JSONObject();
						metaObject.put("name", name);
						metaObject.put("created",
								dataEntity.getProperty("created"));
						meta.append(metaObject.toString());
						meta.append("\n");
					} catch (Exception e) {
					}
				} catch (Exception e) {
					log.warn("Error writing blob", e);
					continue;
				}
			}
			if (filesAdded > 0) {
				ZipEntry entry = new ZipEntry("meta.json");
				zip.putNextEntry(entry);
				zip.write(meta.toString().getBytes("utf-8"));
				zip.closeEntry();
			}
			return filesAdded;
		} catch (Exception e) {
			log.error("Download error", e);
		}
		return 0;
	}
}
