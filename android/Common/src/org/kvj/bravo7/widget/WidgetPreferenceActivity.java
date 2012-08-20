package org.kvj.bravo7.widget;

import java.util.ArrayList;
import java.util.List;

import org.kvj.bravo7.ApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class WidgetPreferenceActivity extends PreferenceActivity {

	private ApplicationContext app = null;

	protected Integer widgetID = null;
	protected String widgetType;
	private int prefID;
	List<WidgetPreference> prefs = new ArrayList<WidgetPreference>();
	String[] ids = new String[0];

	public WidgetPreferenceActivity(ApplicationContext app, String widgetType,
			int prefID) {
		super();
		this.app = app;
		this.widgetType = widgetType;
		this.prefID = prefID;
	}

	protected void setCustomPreferences(String... ids) {
		this.ids = ids;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // Selected
			for (String id : ids) { // For every id
				Preference p = findPreference(id);
				if (null != p && p instanceof WidgetPreference) { // Our case
					WidgetPreference wp = (WidgetPreference) p;
					boolean res = wp.onActivityResult(requestCode, data);
					if (res) { // Stop
						return;
					}
				}
			}
		}
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
			for (String id : ids) { // For every id
				Preference p = findPreference(id);
				if (null != p && p instanceof WidgetPreference) { // Our case
					WidgetPreference wp = (WidgetPreference) p;
					wp.setActivity(this);
				}
			}
		}
	}

	protected void onSave() {
		for (String id : ids) { // For every id
			Preference p = findPreference(id);
			if (null != p && p instanceof WidgetPreference) { // Our case
				WidgetPreference wp = (WidgetPreference) p;
				wp.onFinish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (null != widgetID) {
			getPreferenceManager().getSharedPreferences().edit()
					.putString("type", widgetType).commit();
			onSave();
			app.setWidgetConfig(widgetID, widgetType);
			app.updateWidgets(widgetID);
		}
		super.onBackPressed();
	}
}
