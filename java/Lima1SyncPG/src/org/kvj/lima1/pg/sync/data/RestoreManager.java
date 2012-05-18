package org.kvj.lima1.pg.sync.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreManager {

	private static Logger log = LoggerFactory.getLogger(RestoreManager.class);

	static List<String> getFilesInZip(InputStream stream) throws IOException {
		ZipInputStream zip = new ZipInputStream(stream);
		List<String> files = new ArrayList<String>();
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
			files.add(entry.getName());
			zip.closeEntry();
		}
		zip.close();
		return files;
	}

	static InputStream openFileInZip(InputStream stream, String name)
			throws IOException {
		ZipInputStream zip = new ZipInputStream(stream);
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
			if (entry.getName().equals(name)) {
				return zip;
			}
			zip.closeEntry();
		}
		zip.close();
		return null;
	}

	public static String restoreFromFiles(DataSource ds, String app,
			String user, List<FileItem> items) {
		for (FileItem item : items) {
			if (!item.isFormField()) {
				// File
				try {
					List<String> files = getFilesInZip(item.getInputStream());
					String result = null;
					if (files.contains("meta.json")) {
						result = FileStorage.restoreFiles(ds, app, user, item);
					} else {
						result = DataStorage.restoreData(ds, app, user, item);
					}
					if (null != result) {
						log.error("Restore file {} failed", item.getName());
						return result;
					}
				} catch (Exception e) {
					log.warn(
							"Skip file " + item.getName() + " not a valid one",
							e);
				}
			}
		}
		return null;
	}
}
