package org.kvj.bravo7.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class WidgetUpdateReceiver extends BroadcastReceiver {

	public static final String WIDGET_ID = "id";
	public static final String INTENT_SUFFIX = ".UPDATE_WIDGET";
	private static final String TAG = "WidgetUpdateReceiver";

	public static PendingIntent createUpdateIntent(Context ctx, int widgetID) {
		Intent intent = new Intent(ctx.getPackageName() + INTENT_SUFFIX);
		intent.putExtra(WIDGET_ID, widgetID);
		return PendingIntent.getBroadcast(ctx, widgetID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		try { // Convert errors
			int widgetID = intent.getIntExtra(WIDGET_ID, -1);
			AppWidgetManager manager = AppWidgetManager.getInstance(ctx);
			AppWidgetProviderInfo info = manager.getAppWidgetInfo(widgetID);
			if (info == null) {
				Log.w(TAG, "updateWidgets no info for " + widgetID);
				return;
			}
			AppWidgetProvider provider = (AppWidgetProvider) getClass()
					.getClassLoader().loadClass(info.provider.getClassName())
					.newInstance();
			// Log.i(TAG, "updateWidgets calling update...");
			provider.onUpdate(ctx, manager, new int[] { widgetID });
			Toast.makeText(ctx, "Widget updated", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG, "Error updating widget", e);
		}
	}

}
