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
			db.execSQL("create table if not exists updates ("
					+ "id integer primary key, " + "version_in integer, "
					+ "version_out integer, " + "version text)");
			db.execSQL("create table if not exists data ("
					+ "id integer primary key, "
					+ "status integer default 0, "
					+ "updated integer default 0, "
					+ "own integer default 1, "
					+ "stream text, "
					+ "data text, "
					+ "i0 integer, i1 integer, i2 integer, i3 integer, i4 integer, "
					+ "i5 integer, i6 integer, i7 integer, i8 integer, i9 integer, "
					+ "t0 text, t1 text, t2 text, t3 text, t4 text, "
					+ "t5 text, t6 text, t7 text, t8 text, t9 text)");
			break;
		}
	}

}
