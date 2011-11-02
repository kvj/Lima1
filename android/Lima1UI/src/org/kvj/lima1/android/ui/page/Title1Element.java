package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Title1Element extends UIElement {

	private static final String TAG = "Title1UI";

	@Override
	protected View render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		if (options.empty) {
			return null;
		}
		TextView title = new TextView(element.getContext());
		title.setId(renderer.getNextID());
		title.setTextSize(15);
		LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		title.setText(renderer.inject(config.optString("name"), item));
//		Log.i(TAG, "Created title1: "+title.getText());
		element.addView(title, titleParams);
		return null;
	}

}
