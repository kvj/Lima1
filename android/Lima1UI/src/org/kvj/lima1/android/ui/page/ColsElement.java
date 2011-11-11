package org.kvj.lima1.android.ui.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ColsElement extends UIElement {

	private static final String TAG = "ColsUI";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		JSONArray sizes = config.optJSONArray("size");
		float space = (float) config.optDouble("space", 0);
//		space = 0;
		if (null == flow || sizes == null || flow.length() != sizes.length()) {
			Log.w(TAG, "Invalid cols - flow/size is different");
			return;
		}
//		Log.i(TAG, "Render cols: "+space+", "+flow.length());
		LinearLayout layout = new LinearLayout(element.getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
//			layout.setBackgroundResource(R.drawable.col_bg);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		element.addView(layout, params);
		for (int i = 0; i < flow.length(); i++) {
			if (i>0 && space>0) {
				LinearLayout sp = new LinearLayout(element.getContext());
//					Log.i(TAG, "Add space: "+space);
//					sp.setBackgroundResource(R.drawable.col_bg);
				LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, space);
				layout.addView(sp, spParams);
			}
			JSONObject fl = flow.getJSONObject(i);
			LinearLayout sp = new LinearLayout(element.getContext());
			sp.setOrientation(LinearLayout.VERTICAL);
			sp.setBackgroundResource(R.color.opacity);
			LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, (float) sizes.getDouble(i));
			style(sp, fl, spParams);
			layout.addView(sp, spParams);
			renderer.get(fl.optString("type")).render(renderer, item, fl, sp, options);
//				Log.i(TAG, "Add col: "+sizes.getDouble(i));
		}
	}
	
	@Override
	protected int stretch(int step, Renderer renderer, JSONObject config,
			ViewGroup element) throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		float space = (float) config.optDouble("space", 0);
		int maxh = 0;
		LinearLayout layout = (LinearLayout) element.getChildAt(0);
		for (int i = 0, index = 0; i < flow.length(); i++, index++) {
			if (i>0 && space>0) {
				index++;
			}
			LinearLayout sp = (LinearLayout) layout.getChildAt(index);
			JSONObject fl = flow.getJSONObject(i);
//			Log.i(TAG, "Stretch col: "+flow.length()+", "+i+", "+index+", "+layout.getChildCount()+", "+space+", "+config);
			int h = renderer.get(fl.optString("type")).stretch(step, renderer, fl, sp);
			if (h>maxh) {
				maxh = h;
			}
		}
		layout.setTag(maxh);
		return maxh;
	}
	
	@Override
	protected void fill(int h, Renderer renderer, JSONObject item, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		JSONArray flow = config.optJSONArray("flow");
		float space = (float) config.optDouble("space", 0);
		LinearLayout layout = (LinearLayout) element.getChildAt(0);
		int maxh = (Integer) layout.getTag();
		for (int i = 0, index = 0; i < flow.length(); i++, index++) {
			if (i>0 && space>0) {
				index++;
			}
			LinearLayout sp = (LinearLayout) layout.getChildAt(index);
			JSONObject fl = flow.getJSONObject(i);
			renderer.get(fl.optString("type")).fill(maxh, renderer, item, fl, sp, options);
		}
	}

}
