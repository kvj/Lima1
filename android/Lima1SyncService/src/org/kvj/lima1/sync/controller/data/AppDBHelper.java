package org.kvj.lima1.sync.controller.data;

import org.kvj.bravo7.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AppDBHelper extends DBHelper {

	public AppDBHelper(Context context, String path) {
		super(context, path, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void migrate(SQLiteDatabase db, int version) {
		switch (version) {
		case 1:
			db.execSQL("create table if not exists updates (" + "id integer primary key, " + "version_in integer, "
					+ "version_out integer, " + "version text)");
			db.execSQL("create table if not exists uploads (id integer primary key, path text, name text, status integer)");
			break;
		}
	}

}
