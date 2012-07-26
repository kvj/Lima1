package org.kvj.bravo7.widget;

import org.kvj.bravo7.ApplicationContext;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class WidgetPreferenceActivity extends PreferenceActivity {

	private ApplicationContext app = null;

	Integer widgetID = null;
	private String widgetType;
	private int prefID;

	public WidgetPreferenceActivity(ApplicationContext app, String widgetType,
			int prefID) {
		super();
		this.app = app;
		this.widgetType = widgetType;
		this.prefID = prefID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		widgetID = app.getWidgetConfigID(getIntent());
		if (null != widgetID) {
			app.setWidgetConfigDone(this);
		}
		if (null == widgetID && getIntent().getExtras() != null) {
			widgetID = getIntent().getExtras().getInt("id");
		}
		if (null != widgetID) { // Have widget ID
			getPreferenceManager().setSharedPreferencesName(
					"widget_" + widgetID);
			addPreferencesFromResource(prefID);
		}
	}

	@Override
	public void onBackPressed() {
		if (null != widgetID) {
			getPreferenceManager().getSharedPreferences().edit()
					.putString("type", widgetType).commit();
			app.setWidgetConfig(widgetID, widgetType);
			app.updateWidgets(widgetID);
		}
		super.onBackPressed();
	}
}
