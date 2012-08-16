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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

public abstract class RemoteServicesCollector<T extends IInterface> {

	public class PluginConnection {
		RemoteServiceConnector<T> connector = null;
	}

	public interface PluginFilter {
		public boolean filter(String category, Bundle meta);
	}

	public static class APIPluginFilter implements PluginFilter {

		@Override
		public boolean filter(String category, Bundle meta) {
			if (meta.containsKey("api")) { // Have API
				try { // Conversion errors
					int api = meta.getInt("api");
					if (api > Build.VERSION.SDK_INT) { // Not suitable
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

	}

	private static final String TAG = "RemotePlugins";

	private Context ctx = null;
	private String action = null;
	private Map<String, List<PluginConnection>> plugins = new HashMap<String, List<PluginConnection>>();
	private PackageBroadcastReceiver receiver = null;
	private List<PluginFilter> filters = new ArrayList<PluginFilter>();

	class PackageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			collectPlugins(intent.getAction(), intent.getDataString());
		}
	}

	public RemoteServicesCollector(Context ctx, String action,
			PluginFilter... filters) {
		this.ctx = ctx;
		this.action = action;
		if (null != filters) { // Have filters
			for (PluginFilter filter : filters) { // Add filters
				addPluginFilter(filter);
			}
		}
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
					baseIntent, PackageManager.GET_RESOLVED_FILTER
							| PackageManager.GET_META_DATA);
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
				boolean filteredOut = false;
				String category = filter.getCategory(0);
				synchronized (filters) { // Lock for modifications
					for (PluginFilter f : filters) { // Run filter
						if (null != sinfo.metaData
								&& !f.filter(category, sinfo.metaData)) {
							// Filtered out
							filteredOut = true;
							break;
						}
					}
				}
				if (filteredOut) { // Skip plugin
					Log.i(TAG, "Plugin " + category + " filtered out");
				}
				plugin.connector = new RemoteServiceConnector<T>(ctx, action,
						category) {

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

	public void addPluginFilter(PluginFilter filter) {
		synchronized (filters) { // Lock for modifications
			filters.add(filter);
		}
	}

}
