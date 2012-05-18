package org.kvj.lima1.pg.sync.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		algorithm.update(new String(password + SALT).getBytes());
		byte[] messageDigest = algorithm.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}

		return hexString.toString();
	}

	public static String authorizeUser(DataSource ds, String username,
			String password, String token) {
		Connection c = null;
		try {
			String uName = username.toLowerCase().trim();
			String pass = passwordToHash(password);
			c = ds.getConnection();
			PreparedStatement existing = c
					.prepareStatement("select id, password from users where username=?");
			existing.setString(1, uName);
			ResultSet set = existing.executeQuery();
			long id = 0;
			if (!set.next()) {
				// Create user
				id = DAO.nextID(c);
				PreparedStatement createUser = c
						.prepareStatement("insert into users (id, username, password, created) values (?, ?, ?, ?)");
				createUser.setLong(1, id);
				createUser.setString(2, uName);
				createUser.setString(3, pass);
				createUser.setLong(4, System.currentTimeMillis());
				createUser.execute();
			} else {
				// Existing user
				if (!pass.equals(set.getString(2))) {
					log.warn("Password in incorrect for user " + uName);
					return "Password is incorrect";
				}
				id = set.getLong(1);
			}
			log.info("Storing token: " + token);
			PreparedStatement createToken = c
					.prepareStatement("insert into tokens (id, user_id, token, issued, accessed) values (?, ?, ?, ?, ?)");
			createToken.setLong(1, DAO.nextID(c));
			createToken.setLong(2, id);
			createToken.setString(3, token);
			createToken.setLong(4, System.currentTimeMillis());
			createToken.setLong(5, System.currentTimeMillis());
			createToken.execute();
			return null;
		} catch (Exception e) {
			log.error("Users error", e);
			return "DB error";
		} finally {
			DAO.closeConnection(c);
		}
	}

	public static long findUserByName(Connection c, String username)
			throws SQLException {
		PreparedStatement st = c
				.prepareStatement("select id from users where username=?");
		st.setString(1, username);
		ResultSet set = st.executeQuery();
		if (set.next()) {
			return set.getLong(1);
		}
		throw new SQLException("User " + username + " not found");
	}

	public static String verifyToken(DataSource ds, String token) {
		Connection c = null;
		try {
			c = ds.getConnection();
			PreparedStatement searchToken = c
					.prepareStatement("select t.id, u.username from tokens t, users u where t.user_id=u.id and t.token=?");
			searchToken.setString(1, token);
			ResultSet set = searchToken.executeQuery();
			if (!set.next()) {
				// Token not found/expired - error
				log.warn("Token {} not found - error", token);
				return null;
			}
			// Update token
			PreparedStatement updateToken = c
					.prepareStatement("update tokens set accessed=? where id=?");
			updateToken.setLong(1, System.currentTimeMillis());
			updateToken.setLong(2, set.getLong(1));
			updateToken.execute();
			return set.getString(2);
		} catch (Exception e) {
			log.error("Token error", e);
			return null;
		} finally {
			DAO.closeConnection(c);
		}
	}
}
