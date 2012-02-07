package org.kvj.lima1.android.ui.page;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ListElement extends UIElement {

	private LinearLayout newElement(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options,
			boolean disabled) throws JSONException {
		LinearLayout el = new LinearLayout(element.getContext());
		// if (!disabled) {
		// el.setOnTouchListener(new OnTouchListener() {
		//
		// public boolean onTouch(View v, MotionEvent event) {
		// Log.i(TAG,
		// "Touch: " + event.getAction() + ", "
		// + event.getAxisValue(MotionEvent.AXIS_X));
		// return false;
		// }
		// });
		// el.setOnLongClickListener(new OnLongClickListener() {
		//
		// public boolean onLongClick(View v) {
		// Log.i(TAG, "Starting drag...");
		// ClipData.Item item = new ClipData.Item("test");
		// ClipData dragData = new ClipData("Dragging item...",
		// new String[] { "text/plain" }, item);
		// // v.startDrag(dragData, null, "", 0);
		// return false;
		// }
		// });
		// }
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		el.setOrientation(LinearLayout.VERTICAL);
		el.setBackgroundResource(R.color.opacity);
		style(el, config.optJSONObject("config"), params);
		element.addView(el, params);
		LinearLayout parent = (LinearLayout) element;
		parent.forceLayout();
		UIElementOptions options2 = new UIElementOptions();
		options2.disabled = disabled;
		renderer.get(null).render(renderer, item, config, el, options2);
		return el;
	}

	private static final String TAG = "ListUI";

	@Override
	protected int grow(int height, Renderer renderer, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		String area = config.optString("area", "main");
		int emptyViewHeight = getFullHeight(element.getChildAt(element
				.getChildCount() - 1));
		int nowHeight = getFullHeight(element);

		int added = (int) Math.floor((height - nowHeight) / emptyViewHeight);
		// Log.i(TAG, "Grow: height: " + height + ", nowHeight: " + nowHeight
		// + ", empty: " + emptyViewHeight + ", added: " + added);
		for (int i = 0; i < added; i++) {
			JSONObject empty = new JSONObject();
			empty.put("area", area);
			nowHeight += emptyViewHeight;
			newElement(renderer, empty, config, element, options, true);
		}
		return nowHeight;
	}

	@Override
	protected boolean canGrow(JSONObject config) {
		if (!"yes".equals(config.optString("grow", "yes"))) {
			return false;
		}
		return true;
	}

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		String area = config.optString("area", "main");
		List<JSONObject> items = renderer.items(area);
		for (int i = 0; i < items.size(); i++) {
			JSONObject itm = items.get(i);
			newElement(renderer, itm, config, element, options, false);
		}
		JSONObject empty = new JSONObject();
		empty.put("area", area);
		newElement(renderer, empty, config, element, options, false);
		element.setTag(R.id.empty_items, new Integer(0));
	}

}
