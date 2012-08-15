package org.kvj.bravo7.ipc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
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

	private Context ctx = null;
	private String action = null;
	private Map<String, List<PluginConnection>> plugins = new HashMap<String, List<PluginConnection>>();
	private PackageBroadcastReceiver receiver = null;

	class PackageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			collectPlugins(intent.getAction(), intent.getDataString());
		}
	}

	public RemoteServicesCollector(Context ctx, String action) {
		this.ctx = ctx;
		this.action = action;
		receiver = new PackageBroadcastReceiver();
		IntentFilter packageFilter = new IntentFilter();
		packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		packageFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		packageFilter.addCategory(Intent.CATEGORY_DEFAULT);
		packageFilter.addDataScheme("package");
		ctx.registerReceiver(receiver, packageFilter);
		collectPlugins(Intent.ACTION_PACKAGE_ADDED, null);
	}

	private void collectPlugins(String packageAction, String changedPackage) {
		if (null != changedPackage) {
			List<PluginConnection> list = plugins.get(changedPackage);
			if (null != list) { // Have plugins - stop
				for (PluginConnection plugin : list) { // Call stop()
					plugin.connector.stop();
				}
				list.clear();
			}
		}
		if (!Intent.ACTION_PACKAGE_REMOVED.equals(packageAction)) {
			// Not removed
			// Discover
			PackageManager packageManager = ctx.getPackageManager();
			Intent baseIntent = new Intent(action);
			if (null != changedPackage) {
				baseIntent.setPackage(changedPackage);
			}
			baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
			List<ResolveInfo> resolves = packageManager.queryIntentServices(
					baseIntent, PackageManager.GET_RESOLVED_FILTER);
			if (null == resolves) {
				resolves = new ArrayList<ResolveInfo>();
			}
			for (ResolveInfo info : resolves) { // Check every found item
				ServiceInfo sinfo = info.serviceInfo;
				IntentFilter filter = info.filter;
				if (null == sinfo || null == filter
						|| 0 == filter.countCategories()) { // Invalid data
					continue;
				}
				List<PluginConnection> list = plugins.get(sinfo.packageName);
				if (null == list) { // List not created - new
					list = new ArrayList<PluginConnection>();
					plugins.put(sinfo.packageName, list);
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
		onChange();
	}

	public void onChange() {
	}

	abstract public T castAIDL(IBinder binder);

	public void stop() {
		ctx.unregisterReceiver(receiver);
	}

	public List<T> getPlugins() {
		List<T> result = new ArrayList<T>();
		synchronized (plugins) {
			for (List<PluginConnection> p : plugins.values()) {
				for (PluginConnection pc : p) {
					T remote = pc.connector.getRemote();
					if (null != remote) {
						result.add(remote);
					}
				}
			}
		}
		return result;
	}

}
