package org.kvj.bravo7.ipc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;

abstract public class RemoteServiceConnector<T extends IInterface> implements ServiceConnection {

	private static final String TAG = "RemoteConnector";
	private T remote = null;
	private PackageBroadcastReceiver packageBroadcastReceiver = null;
	private IntentFilter packageFilter = null;
	private Context ctx = null;
	private String action = null;
	private String category = null;
	private boolean active = true;

	class PackageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.i(TAG,
			// "Received: " + intent.getAction() + ", "
			// + intent.getDataString());
			if (null == remote && active) { // Reconnect
				connect();
			}
		}
	}

	public RemoteServiceConnector(Context ctx, String action, String category) {
		this.ctx = ctx;
		this.action = action;
		this.category = category;
		packageBroadcastReceiver = new PackageBroadcastReceiver();
		packageFilter = new IntentFilter();
		packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		packageFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		packageFilter.addCategory(Intent.CATEGORY_DEFAULT);
		packageFilter.addDataScheme("package");
		ctx.registerReceiver(packageBroadcastReceiver, packageFilter);
		connect();
	}

	protected void connect() {
		// Log.i(TAG, "Connecting to " + action);
		Intent bindIntent = new Intent(action);
		if (null != category) { // Have category
			bindIntent.addCategory(category);
		}
		onBeforeConnect(bindIntent);
		ctx.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
	}

	protected void onBeforeConnect(Intent intent) {

	}

	public T getRemote() {
		return remote;
	}

	public void stop() {
		active = false;
		ctx.unregisterReceiver(packageBroadcastReceiver);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		remote = castAIDL(service);
		onConnect();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		remote = null;
		onDisconnect();
	}

	abstract public T castAIDL(IBinder binder);

	public void onConnect() {

	}

	public void onDisconnect() {

	}
}
