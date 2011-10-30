package org.kvj.lima1.gae.sync.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.files.AppEngineFile.FileSystem;

public class UserStorage {

	private static Logger log = LoggerFactory.getLogger(UserStorage.class);
	private static final String SALT = "lima1sync";
	
	private static String passwordToHash(String password) {
        MessageDigest algorithm;
		try {
			algorithm = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			log.error("Error in password hashing", e);
			return password;
		}
        algorithm.reset();
        algorithm.update(new String(password+SALT).getBytes());
        byte[] messageDigest = algorithm.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }

        return hexString.toString();
	}
	
	public static String authorizeUser(String username, String password, String token) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			String uName = username.toLowerCase().trim();
			String pass = passwordToHash(password);
			Query existing = new Query("User").addFilter("username", FilterOperator.EQUAL, uName);
			Entity user = datastore.prepare(existing).asSingleEntity();
			if (null == user) {
				log.info("User {} isn't exists - creating", uName);
				user = new Entity("User");
				user.setProperty("username", uName);
				user.setProperty("password", pass);
				user.setProperty("created", new Date().getTime());
				datastore.put(txn, user);
				txn.commit();
				txn = datastore.beginTransaction();
			}
			log.info("Checking password for entity: {}", user.getKey());
			if (!pass.equals(user.getProperty("password"))) {
				log.warn("Password in incorrect for user "+uName+": "+pass);
				return "Password is incorrect";
			}
			log.info("Storing token");
			Entity tokenEntity = new Entity("Token");
			tokenEntity.setProperty("token", token);
			tokenEntity.setProperty("user", user.getKey());
			tokenEntity.setProperty("issued", new Date().getTime());
			tokenEntity.setProperty("accessed", new Date().getTime());
			datastore.put(txn, tokenEntity);
		    txn.commit();
			log.info("Token created");
		    return null;
		} catch (Exception e) {
			log.error("Users error", e);
			return "DB error";
		} finally {
		    if (txn.isActive()) {
		        txn.rollback();
		    }
		}	
	}
	
	public static String verifyToken(String token) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			Query searchToken = new Query("Token").
					addFilter("token", FilterOperator.EQUAL, token);
			Entity tokenEntity = datastore.prepare(searchToken).asSingleEntity();
			if (null == tokenEntity) {
				log.info("Token {} not found - error", token);
				return null;
			}
			log.info("Updating token");
			tokenEntity.setProperty("accessed", new Date().getTime());
			datastore.put(txn, tokenEntity);
		    txn.commit();
			log.info("Token OK");
			Entity userEntity = datastore.get((Key) tokenEntity.getProperty("user"));
		    return (String) userEntity.getProperty("username");
		} catch (Exception e) {
			log.error("Token error", e);
			return null;
		} finally {
		    if (txn.isActive()) {
		        txn.rollback();
		    }
		}	
	}
}
