package org.kvj.lima1.sync.controller;

import org.kvj.bravo7.SuperService;
import org.kvj.lima1.sync.ConnectionsActivity;
import org.kvj.lima1.sync.Lima1SyncApp;
import org.kvj.lima1.sync.R;
import org.kvj.lima1.sync.SyncServiceInfo;

import android.app.Service;
import android.content.Intent;

public class BackgroundSyncService extends
		SuperService<SyncController, Lima1SyncApp> {

	public BackgroundSyncService() {
		super(SyncController.class, "Lima1");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		raiseNotification(R.drawable.ic_launcher, "Started",
				ConnectionsActivity.class);
		sendBroadcast(new Intent(SyncServiceInfo.STARTED_INTENT));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

}
