package org.kvj.bravo7.widget;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class WidgetPreference extends Preference {

	protected WidgetPreferenceActivity activity = null;

	public WidgetPreference(Context context, AttributeSet attrs, int resID) {
		super(context, attrs);
		setLayoutResource(resID);
	}

	public void setActivity(WidgetPreferenceActivity activity) {
		this.activity = activity;
	}

	protected void onFinish() {
	}

	protected boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}

}
