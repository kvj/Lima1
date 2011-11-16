package org.kvj.lima1.sync.controller;

import org.kvj.bravo7.ApplicationContext;
import org.kvj.bravo7.SuperService;
import org.kvj.lima1.sync.ConnectionsActivity;
import org.kvj.lima1.sync.R;

public class BackgroundSyncService extends SuperService<SyncController> {

	private ApplicationContext appContext = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		appContext = ApplicationContext.getInstance(this);
		controller = new SyncController();
		appContext.publishBean(controller);
		raiseNotification(R.drawable.ic_launcher, "Started", ConnectionsActivity.class);
	}
	
}
