package org.kvj.lima1.android.ui.controller;

import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;
import org.kvj.lima1.sync.SyncService;
import org.kvj.lima1.sync.SyncServiceConnection;
import org.kvj.lima1.sync.SyncServiceInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;

public class Lima1Controller {

	class _SyncServiceConnection extends SyncServiceConnection {

		public _SyncServiceConnection(String application) {
			super(application);
		}

		public void onConnected() {
			try {
				Log.i(TAG, "Sync service connected: " + connection.message());
				db = connection;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		};

		public void onDisconnected() {
			Log.i(TAG, "Sync service disconnected");
			db = null;
		};
	}

	SyncService db = null;

	protected static final String TAG = "Lima1";
	private _SyncServiceConnection sync = null;

	BroadcastReceiver startReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Got sync service start message");
			if (null == db) {
				sync.connect(Lima1App.getInstance());
			}
		}
	};

	public Lima1Controller() {
		Log.i(TAG, "Controller is ready");
		sync = new _SyncServiceConnection("lima1");
		sync.connect(Lima1App.getInstance());
		Lima1App.getInstance().registerReceiver(startReceiver,
				new IntentFilter(SyncServiceInfo.STARTED_INTENT));
	}

	public boolean isAvailable() {
		return null != db;
	}

	public String sync() {
		if (null != db) {
			try {
				db.startSync();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "DB not available";
	}

	public PJSONObject[] getPages(boolean archived) {
		if (null == db) {
			return null;
		}
		try {
			QueryOperator op = new QueryOperator("archived", 1);
			if (!archived) {
				op = new QueryOperator("archived", 1, "<>");
			}
			return db
					.query("sheets", new QueryOperator[] { op }, "place", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
