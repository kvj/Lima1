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
	protected void render(Renderer renderer, JSONObject item, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		if (config.has("defaults")) {
			renderer.applyDefaults(config.getJSONObject("defaults"), item);
		}
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = new LinearLayout(element.getContext());
			el.setBackgroundResource(R.color.opacity);
			el.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			style(el, fl, params);
			element.addView(el, params);
			renderer.get(fl.optString("type")).render(renderer, item, fl, el, options);
		}
	}
	
	@Override
	protected void fill(int height, Renderer renderer, JSONObject item, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = (LinearLayout) element.getChildAt(i);
			int h = (Integer) el.getTag(R.id.simple_height);
			renderer.get(fl.optString("type")).fill(h, renderer, item, fl, el, options);
		}
	}
	
	@Override
	protected int stretch(int step, Renderer renderer, JSONObject config,
			ViewGroup element) throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		int h = 0;
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = (LinearLayout) element.getChildAt(i);
//			Log.i(TAG, "Stretch simple: "+el+", fl: "+fl+", "+element.getChildCount()+", "+i);
			int height = renderer.get(fl.optString("type")).stretch(step, renderer, fl, el);
			el.setTag(R.id.simple_height, height);
			h += height;
//			Log.i(TAG, "Stretch simple: "+h+", "+step);
		}
		return h;
	}

}
