package org.kvj.bravo7.widget;

import org.kvj.bravo7.ApplicationContext;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class WidgetProvider extends AppWidgetProvider {

	private static final String TAG = "WidgetProvider";
	protected ApplicationContext app = null;

	public WidgetProvider(ApplicationContext context) {
		this.app = context;
	}

	protected String getString(SharedPreferences data, int name, int def) {
		return data.getString(app.getResources().getString(name), app
				.getResources().getString(def));
	}

	protected void setInt(SharedPreferences data, int name, int value) {
		data.edit()
				.putString(app.getResources().getString(name),
						Integer.toString(value)).commit();
	}

	protected int getInt(SharedPreferences data, int name, int def) {
		int defInt = -1;
		try {
			defInt = Integer
					.parseInt(data.getString(app.getResources().getString(def),
							"-1"), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return Integer.parseInt(getString(data, name, def), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defInt;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(TAG, "onUpdate: " + appWidgetIds.length);
		for (int i = 0; i < appWidgetIds.length; i++) {
			// Call update for every widget
			int id = appWidgetIds[i];
			update(id, appWidgetManager, new Bundle());
		}
	}

	private boolean update(int id, AppWidgetManager appWidgetManager,
			Bundle extras) {
		SharedPreferences prefs = app.getWidgetConfig(id);
		if (null == prefs) { // Skip
			Log.w(TAG, "onUpdate: no prefs " + id);
			return false;
		}
		RemoteViews views = update(prefs, id, extras);
		if (null != views) { // Have views - update
			appWidgetManager.updateAppWidget(id, views);
		} else {
			Log.w(TAG, "onUpdate: no views " + id);
			return false;
		}
		return true;
	}

	public void update(int id, Bundle extras) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(app);
		update(id, appWidgetManager, extras);
	}

	abstract protected RemoteViews update(SharedPreferences preferences,
			int id, Bundle data);

}
