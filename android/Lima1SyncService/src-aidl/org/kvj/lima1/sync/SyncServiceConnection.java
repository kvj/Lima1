package org.kvj.lima1.sync;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class SyncServiceConnection implements ServiceConnection {

	protected SyncService connection = null;
	
	public void connect(ContextWrapper wrapper) {
		Intent bindIntent = new Intent(SyncServiceInfo.INTENT);
//		bindIntent.setClassName(SyncServiceInfo.PACKAGE, SyncServiceInfo.SERVICE);
		wrapper.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
	}
	
	public void disconnect(ContextWrapper wrapper) {
		if (connection != null) {
			wrapper.unbindService(this);
		}
	}
	
	public void onServiceConnected(ComponentName arg0, IBinder binder) {
		connection = SyncService.Stub.asInterface(binder);
		onConnected();
	}

	public void onServiceDisconnected(ComponentName arg0) {
		connection = null;
		onDisconnected();
		
	}
	
	public void onConnected() {
		
	}

	public void onDisconnected() {
		
	}

}
