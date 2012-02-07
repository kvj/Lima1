package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

abstract public class UIElement {

	protected static int seq = 0;

	protected int getFullHeight(View view) {
		MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
		return view.getMeasuredHeight() + params.topMargin
				+ params.bottomMargin;
	}

	protected void style(ViewGroup element, JSONObject config,
			MarginLayoutParams layoutParams) {
		if (null == config) {
			return;
		}
		String grid = config.optString("grid");
		String bg = config.optString("bg");
		// String delim = config.optString("delimiter");
		if ("1".equals(grid)) {
			element.setBackgroundResource(R.drawable.grid_1);
			layoutParams.setMargins(0, 2, 0, 2);
		}
		if ("2".equals(grid)) {
			element.setBackgroundResource(R.drawable.grid_2);
			layoutParams.setMargins(0, 3, 0, 3);
		}
		if ("1".equals(bg)) {
			element.setBackgroundResource(R.color.bg1);
		}
		if ("2".equals(bg)) {
			element.setBackgroundResource(R.color.bg2);
		}
	}

	abstract protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException;

	protected boolean canGrow(JSONObject config) {
		return false;
	}

	protected int grow(int height, Renderer renderer, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		return height;
	}
}
