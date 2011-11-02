package org.kvj.lima1.android.ui.page;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ListElement extends UIElement {

	private LinearLayout newElement(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options, boolean disabled) throws JSONException {
		LinearLayout el = new LinearLayout(element.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		el.setOrientation(LinearLayout.VERTICAL);
		element.addView(el, params);
		LinearLayout parent = (LinearLayout) element;
		parent.forceLayout();
		UIElementOptions options2 = new UIElementOptions();
		options2.empty = false;
		options2.disabled = disabled;
		renderer.get(null).render(renderer, item, config, el, options2);
		return el;
	}
	
	private static final String TAG = "ListUI";

	@Override
	protected View render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		String area = config.optString("area", "main");
		if (options.empty) {
			int heightAvail = element.getMeasuredHeight();
			for (int i = 0; i < element.getChildCount(); i++) {
				heightAvail -= element.getChildAt(i).getMeasuredHeight();
			}
			int emptyViewHeight = element.getChildAt(element.getChildCount()-1).getMeasuredHeight();
			int spaceAdded = 0;
			View added = null;
//			Log.i(TAG, "Time to add another element: "+element.getMeasuredHeight()+", "+emptyView.getMeasuredHeight()+" - "+emptyView.getTop()+", "+heightAvail);
			while (spaceAdded<=heightAvail) {
				JSONObject empty = new JSONObject();
				empty.put("area", area);
				added = newElement(renderer, empty, config, element, options, true);
				spaceAdded += emptyViewHeight;
//				Log.i(TAG, "Added empty: "+spaceAdded+", "+heightAvail);
			}
			if (null != added) {
				renderer.lastItems.add(added);
			}
			return added;
		} else {
			List<JSONObject> items = renderer.items(area);
			for (int i = 0; i < items.size(); i++) {
				JSONObject itm = items.get(i);
				newElement(renderer, itm, config, element, options, false);
			}
			JSONObject empty = new JSONObject();
			empty.put("area", area);
			newElement(renderer, empty, config, element, options, false);
		}
		return null;
	}

}
