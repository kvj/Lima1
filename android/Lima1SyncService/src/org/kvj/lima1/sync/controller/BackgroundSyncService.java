package org.kvj.lima1.sync.controller;

import org.kvj.bravo7.SuperActivity;
import org.kvj.bravo7.SuperService;
import org.kvj.lima1.sync.ConnectionsActivity;
import org.kvj.lima1.sync.Lima1SyncApp;
import org.kvj.lima1.sync.R;
import org.kvj.lima1.sync.SyncServiceInfo;
import org.kvj.lima1.sync.controller.SyncController.SyncControllerListener;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;

public class BackgroundSyncService extends
		SuperService<SyncController, Lima1SyncApp> implements
		SyncControllerListener {

	Handler handler = new Handler();

	public BackgroundSyncService() {
		super(SyncController.class, "Lima1");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		controller.setListener(this);
		raiseNotification(R.drawable.ic_st_idle, "Started",
				ConnectionsActivity.class);
		sendBroadcast(new Intent(SyncServiceInfo.STARTED_INTENT));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void syncStarted() {
		raiseNotification(R.drawable.ic_st_sync, "Sync in progress",
				ConnectionsActivity.class);
	}

	@Override
	public void syncCompleted(final String error) {
		raiseNotification(R.drawable.ic_st_idle, error != null ? error
				: "Sync done", ConnectionsActivity.class);
		if (null != error) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					SuperActivity.notifyUser(getApplicationContext(), error);
				}
			});
		}
	}
}
