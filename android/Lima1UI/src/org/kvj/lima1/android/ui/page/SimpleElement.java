package org.kvj.lima1.android.ui.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SimpleElement extends UIElement {

	private static final String TAG = "SimpleUI";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		if (config.has("defaults")) {
			renderer.applyDefaults(config.getJSONObject("defaults"), item);
		}
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		// Log.i(TAG, "Render simple: " + config);
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = new LinearLayout(element.getContext());
			el.setBackgroundResource(R.color.opacity);
			el.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			style(el, fl, params);
			element.addView(el, params);
			renderer.get(fl.optString("type")).render(renderer, item, fl, el,
					options);
		}
	}

	@Override
	protected boolean canGrow(JSONObject config) {
		return true;
	}

	@Override
	protected int grow(int height, Renderer renderer, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		int id = ++seq;
		int fixedHeight = 0;
		int floatHeight = 0;
		int floats = 0;
		JSONArray flow = config.optJSONArray("flow");
		if (null == flow) {
			flow = new JSONArray();
		}
		for (int i = 0; i < flow.length(); i++) {
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout el = (LinearLayout) element.getChildAt(i);
			UIElement type = renderer.get(fl.optString("type"));
			if (type.canGrow(fl)) {
				floats++;
				floatHeight += type.getFullHeight(el);
			} else {
				fixedHeight += type.getFullHeight(el);
			}
		}
		// Log.i(TAG, "Grow[" + id + "]: floats: " + floats + ", height: "
		// + height + ", fixed: " + fixedHeight + ", float: "
		// + floatHeight + ", conf: " + config);
		if (floats > 0) {
			for (int i = 0; i < flow.length(); i++) {
				JSONObject fl = flow.getJSONObject(i);
				LinearLayout el = (LinearLayout) element.getChildAt(i);
				UIElement type = renderer.get(fl.optString("type"));
				if (type.canGrow(fl) && floats > 0) {
					int freeHeight = height - fixedHeight - floatHeight;
					int floatPlus = freeHeight / floats;
					int thisHeight = type.getFullHeight(el);
					// Log.i(TAG, "Before grow[" + id + "]: thisHeight: "
					// + thisHeight + ", plus: " + floatPlus
					// + ", floats: " + floats + ", " + freeHeight + ", "
					// + fl);
					fixedHeight += type.grow(thisHeight + floatPlus, renderer,
							fl, el, options);
					floatHeight -= thisHeight;
					// Log.i(TAG, "After grow[" + id + "]: height: " + floatPlus
					// + " = " + fixedHeight);
					floats--;
				}
			}
		}
		return fixedHeight;
	}

}
