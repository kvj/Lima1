package org.kvj.lima1.pg.sync.data;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorage {

	private static Logger log = LoggerFactory.getLogger(FileStorage.class);

	private static String getDataFolderName(String app, String user) {
		return app + "-" + user;
	}

	private static String upload(Connection c, String app, long userID,
			InputStream file, File newFile, String name, long created) {
		try {
			FileOutputStream out = new FileOutputStream(newFile);
			DAO.copyStream(file, out);
			out.close();
			file.close();
			PreparedStatement insertFile = c
					.prepareStatement("insert into files "
							+ "(id, user_id, app, name, created) "
							+ "values (?, ?, ?, ?, ?)");
			insertFile.setLong(1, DAO.nextID(c));
			insertFile.setLong(2, userID);
			insertFile.setString(3, app);
			insertFile.setString(4, name);
			insertFile.setLong(5, created);
			insertFile.execute();
			return null;
		} catch (Exception e) {
			log.error("Save error", e);
			return "Database error";
		}

	}

	public static String upload(DataSource ds, String app, String user,
			InputStream file, String name) {
		Connection c = null;
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			File newFile = new File(DAO.getUploadFolder(getDataFolderName(app,
					user)), name);
			return upload(c, app, userID, file, newFile, name,
					System.currentTimeMillis());
		} catch (Exception e) {
			log.error("Save error", e);
			return "Database error";
		} finally {
			DAO.closeConnection(c);
		}
	}

	public static File getFile(String app, String user, String name) {
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			File file = new File(DAO.getUploadFolder(getDataFolderName(app,
					user)), name);
			if (file.exists() && file.isFile()) {
				return file;
			}
			throw new Exception("File not found");
		} catch (Exception e) {
			log.error("Download error", e);
			return null;
		}
	}

	public static String removeFile(DataSource ds, String app, String user,
			String name) {
		Connection c = null;
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			File file = new File(DAO.getUploadFolder(getDataFolderName(app,
					user)), name);
			PreparedStatement removeFile = c
					.prepareStatement("delete from files where app=? and user_id=? and name=?");
			removeFile.setString(1, app);
			removeFile.setLong(2, userID);
			removeFile.setString(3, name);
			removeFile.execute();
			if (file.exists()) {
				try {
					file.delete();
				} catch (Exception e) {
					log.error("Error removing file", e);
				}
			}
			return null;
		} catch (Exception e) {
			log.error("Remove file error", e);
			return "Database error";
		} finally {
			DAO.closeConnection(c);
		}
	}

	public static int backupFiles(DataSource ds, String app, String user,
			long from, ZipOutputStream zip) {
		Connection c = null;
		try {
			JSONObject schema = SchemaStorage.getInstance().getSchema(app);
			if (null == schema) {
				throw new Exception("Schema not found");
			}
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			PreparedStatement files = c
					.prepareStatement("select name, created from files "
							+ "where user_id=? and app=? and created>?");
			files.setLong(1, userID);
			files.setString(2, app);
			files.setLong(3, from);
			StringBuilder meta = new StringBuilder();
			int filesAdded = 0;
			ResultSet filesSet = files.executeQuery();
			File folder = DAO.getUploadFolder(getDataFolderName(app, user));
			while (filesSet.next()) {
				String name = filesSet.getString(1);
				File file = new File(folder, name);
				if (!file.exists() || !file.isFile()) {
					log.warn("Skip file {} - not accessible", name);
					continue;
				}
				try {
					ZipEntry entry = new ZipEntry(name);
					zip.putNextEntry(entry);
					FileInputStream fis = new FileInputStream(file);
					DAO.copyStream(fis, zip);
					fis.close();
					zip.closeEntry();
					filesAdded++;
					zip.flush();
					JSONObject metaObject = new JSONObject();
					metaObject.put("name", name);
					metaObject.put("created", filesSet.getLong(2));
					meta.append(metaObject.toString());
					meta.append("\n");
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
		} finally {
			DAO.closeConnection(c);
		}
		return 0;
	}

	private static Dimension resize(int width, int height, int newWidth) {
		if (width > newWidth) {
			float ratio = (float) newWidth / (float) width;
			return new Dimension(newWidth, (int) (height * ratio));
		}
		return null;
	}

	public static boolean resizeImageFile(File file, int width, OutputStream out) {
		try {
			BufferedImage image = ImageIO.read(file);
			if (null == image) {
				throw new Exception("Invalid image");
			}
			int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: image.getType();
			Dimension newSize = resize(image.getWidth(), image.getHeight(),
					width);
			if (null == newSize) {
				// Resize not necessary
				return false;
			}
			BufferedImage resizeImage = new BufferedImage(
					(int) newSize.getWidth(), (int) newSize.getHeight(), type);
			Graphics2D g = resizeImage.createGraphics();
			g.setComposite(AlphaComposite.Src);

			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(image, 0, 0, resizeImage.getWidth(),
					resizeImage.getHeight(), null);
			g.dispose();
			String fileType = "jpg";
			int dotIndex = file.getName().lastIndexOf(".");
			if (dotIndex != -1) {
				fileType = file.getName().substring(dotIndex + 1);
			}
			ImageIO.write(resizeImage, fileType, out);
			return true;
		} catch (Exception e) {
			log.error("Error resizing image", e);
		}
		return false;
	}

	static void clearCache(Connection c, String app, String user)
			throws IOException, SQLException {
		File folder = DAO.getUploadFolder(getDataFolderName(app, user));
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				file.delete();
			}
		}
		long userID = UserStorage.findUserByName(c, user);
		PreparedStatement st = c
				.prepareStatement("delete from files where user_id=? and app=?");
		st.setLong(1, userID);
		st.setString(2, app);
		st.execute();
	}

	public static String restoreFiles(DataSource ds, String app, String user,
			FileItem fileItem) {
		Connection c = null;
		try {
			List<String> files = RestoreManager.getFilesInZip(fileItem
					.getInputStream());
			InputStream meta = RestoreManager.openFileInZip(
					fileItem.getInputStream(), "meta.json");
			if (null == meta) {
				throw new Exception("meta.json not found");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(meta,
					"utf-8"));
			String line = null;
			Map<String, Long> metaMap = new HashMap<String, Long>();
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				try {
					JSONObject obj = new JSONObject(line);
					metaMap.put(obj.getString("name"), obj.getLong("created"));
				} catch (Exception e) {
					log.warn("Error reading JSON", e);
				}
			}
			br.close();
			c = ds.getConnection();
			long userID = UserStorage.findUserByName(c, user);
			for (String fileName : files) {
				Long created = metaMap.get(fileName);
				if (null == created) {
					log.warn("Skip file {}", fileName);
				} else {
					InputStream zip = RestoreManager.openFileInZip(
							fileItem.getInputStream(), fileName);
					if (null == zip) {
						throw new Exception("Error in zip");
					}
					File file = new File(DAO.getUploadFolder(getDataFolderName(
							app, user)), fileName);
					String result = upload(c, app, userID, zip, file, fileName,
							created);
					if (null != result) {
						return result;
					}
				}
			}
			return null;
		} catch (Exception e) {
			log.error("Error restoring files", e);
		} finally {
			DAO.closeConnection(c);
		}
		return "Restore files error";
	}
}
