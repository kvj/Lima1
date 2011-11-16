package org.kvj.lima1.sync.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent i = new Intent(context, BackgroundSyncService.class);
		context.startService(i);
	}

}
