package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.ViewGroup;

public class CalendarElement extends UIElement {

	protected static final String TAG = "Calendar";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		// LinearLayout.LayoutParams params = new LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// CalendarView calendar = new CalendarView(element.getContext());
		// element.addView(calendar, params);
		// calendar.setOnDateChangeListener(new OnDateChangeListener() {
		//
		// public void onSelectedDayChange(CalendarView view, int year,
		// int month, int dayOfMonth) {
		// Log.i(TAG, "Date: " + year + ", " + month + ", " + dayOfMonth);
		// }
		// });
	}

}
