package org.kvj.lima1.android.ui.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ColsElement extends UIElement {

	private static final String TAG = "ColsUI";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		JSONArray sizes = config.optJSONArray("size");
		float space = (float) config.optDouble("space", 0);
		// space = 0;
		if (null == flow || sizes == null || flow.length() != sizes.length()) {
			Log.w(TAG, "Invalid cols - flow/size is different");
			return;
		}
		// Log.i(TAG, "Render cols: "+space+", "+flow.length());
		LinearLayout layout = new LinearLayout(element.getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// layout.setBackgroundResource(R.drawable.col_bg);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		element.addView(layout, params);
		for (int i = 0; i < flow.length(); i++) {
			if (i > 0 && space > 0) {
				LinearLayout sp = new LinearLayout(element.getContext());
				// Log.i(TAG, "Add space: "+space);
				// sp.setBackgroundResource(R.drawable.col_bg);
				LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(
						0, LayoutParams.MATCH_PARENT, space);
				layout.addView(sp, spParams);
			}
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout sp = new LinearLayout(element.getContext());
			sp.setOrientation(LinearLayout.VERTICAL);
			sp.setBackgroundResource(R.color.opacity);
			LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(
					0, LayoutParams.MATCH_PARENT, (float) sizes.getDouble(i));
			styleCol(sp, fl, spParams);
			layout.addView(sp, spParams);
			renderer.get(fl.optString("type")).render(renderer, item, fl, sp,
					options);
			// Log.i(TAG, "Add col: "+sizes.getDouble(i));
		}
	}

	private void styleCol(ViewGroup sp, JSONObject config,
			MarginLayoutParams layoutParams) {
		String bg = config.optString("bg");
		String line = config.optString("line");
		if ("".equals(bg) && "".equals(line)) {
			return;
		}
		if ("1".equals(bg) && "".equals(line)) {
			sp.setBackgroundResource(R.drawable.col_g);
			return;
		}
		if ("".equals(bg) && "1".equals(line)) {
			sp.setBackgroundResource(R.drawable.line_w1);
			return;
		}
		Log.w(TAG, "Undefined design: " + line + ", " + bg);
	}

	@Override
	protected boolean canGrow(JSONObject config) {
		return true;
	}

	@Override
	protected int grow(int height, Renderer renderer, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		float space = (float) config.optDouble("space", 0);
		LinearLayout layout = (LinearLayout) element.getChildAt(0);
		// Log.i(TAG, "Fill cols: " + maxh + " / " + flow.length() + " - " +
		// space);
		int maxHeight = 0;
		for (int i = 0, index = 0; i < flow.length(); i++, index++) {
			if (i > 0 && space > 0) {
				index++;
			}
			LinearLayout el = (LinearLayout) layout.getChildAt(index);
			JSONObject fl = flow.getJSONObject(i);
			UIElement type = renderer.get(fl.optString("type"));
			int h = 0;
			if (type.canGrow(fl)) {
				h = type.grow(height, renderer, fl, el, options);
			} else {
				h = type.getFullHeight(el);
			}
			if (h > maxHeight) {
				maxHeight = h;
			}
		}
		return maxHeight;
	}

}
