package org.kvj.bravo7.ipc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.IInterface;

public abstract class RemoteServicesCollector<T extends IInterface> {

	class PluginConnection {
		RemoteServiceConnector<T> connector = null;
	}

	private ContextWrapper ctx = null;
	private String action = null;
	private Map<String, List<PluginConnection>> plugins = new HashMap<String, List<PluginConnection>>();
	private PackageBroadcastReceiver receiver = null;

	class PackageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			collectPlugins(intent);
		}
	}

	public RemoteServicesCollector(ContextWrapper ctx, String action) {
		this.ctx = ctx;
		this.action = action;
		receiver = new PackageBroadcastReceiver();
		IntentFilter packageFilter = new IntentFilter();
		packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		packageFilter.addCategory(Intent.CATEGORY_DEFAULT);
		packageFilter.addDataScheme("package");
		ctx.registerReceiver(receiver, packageFilter);
	}

	private void collectPlugins(Intent intent) {
		List<PluginConnection> list = plugins.get(intent.getPackage());
		if (null != list) { // Have plugins - stop
			for (PluginConnection plugin : list) { // Call stop()
				plugin.connector.stop();
			}
		}
		if (!Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			// Not removed
			if (null == list) { // List not created - new
				list = new ArrayList<PluginConnection>();
			}
			// Discover
			PackageManager packageManager = ctx.getPackageManager();
			Intent baseIntent = new Intent(action);
			baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
			List<ResolveInfo> resolves = packageManager.queryIntentServices(
					baseIntent, PackageManager.GET_RESOLVED_FILTER);
			for (ResolveInfo info : resolves) { // Check every found item
				ServiceInfo sinfo = info.serviceInfo;
				IntentFilter filter = info.filter;
				if (null == sinfo || null == filter
						|| 0 == filter.countCategories()) { // Invalid data
					continue;
				}
				PluginConnection plugin = new PluginConnection();
				plugin.connector = new RemoteServiceConnector<T>(ctx, action,
						filter.getCategory(0)) {

					@Override
					public T castAIDL(IBinder binder) {
						return RemoteServicesCollector.this.castAIDL(binder);
					}
				};
				list.add(plugin);
			}
		}
	}

	abstract public T castAIDL(IBinder binder);

	public void stop() {
		ctx.unregisterReceiver(receiver);
	}

}
