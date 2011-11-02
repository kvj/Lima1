package org.kvj.lima1.android.ui.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SimpleElement extends UIElement {

	private static final String TAG = "SimpleUI";

	@Override
	protected View render(Renderer renderer, JSONObject item, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		if (config.has("defaults") && !options.empty) {
			renderer.applyDefaults(config.getJSONObject("defaults"), item);
		}
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = (LinearLayout) element.getChildAt(i);
			if (null == el) {
				el = new LinearLayout(element.getContext());
				el.setOrientation(LinearLayout.VERTICAL);
//				el.setMinimumHeight(50);
//				el.setBackgroundResource(R.drawable.col_bg);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				element.addView(el, params);
			}
//			Log.i(TAG, "Render simple: "+fl.optString("type")+", "+i+", "+element.getChildCount());
			renderer.get(fl.optString("type")).render(renderer, item, fl, el, options);
		}
		return null;
	}

}
