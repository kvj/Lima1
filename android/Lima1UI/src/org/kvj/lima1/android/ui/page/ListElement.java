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
		LinearLayout el = new LinearLayout(element.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		el.setOrientation(LinearLayout.VERTICAL);
		el.setBackgroundResource(R.color.opacity);
		// style(el, config.optJSONObject("config"), params);
		element.addView(el, params);
		LinearLayout parent = (LinearLayout) element;
		parent.forceLayout();
		UIElementOptions options2 = new UIElementOptions();
		options2.disabled = disabled;
		options2.type = "notes";
		renderer.get(null).render(renderer, item, config.getJSONObject("item"),
				el, options2);
		return el;
	}

	private static final String TAG = "ListUI";

	@Override
	protected int grow(int height, Renderer renderer, JSONObject config,
			ViewGroup element, UIElementOptions options) throws JSONException {
		ViewGroup root = (ViewGroup) element.getChildAt(0);
		String area = config.optString("area", "main");
		int emptyViewHeight = getFullHeight(root.getChildAt(root
				.getChildCount() - 1));
		if (root.getChildCount() == 1
				&& !"".equals(config.optString("delimiter"))) {
			emptyViewHeight += element.getContext().getResources()
					.getDisplayMetrics().density;
		}
		int nowHeight = getFullHeight(root);

		int added = (int) Math.floor((height - nowHeight) / emptyViewHeight);
		// Log.i(TAG, "Grow: height: " + height + ", nowHeight: " + nowHeight
		// + ", empty: " + emptyViewHeight + ", added: " + added);
		for (int i = 0; i < added; i++) {
			JSONObject empty = new JSONObject();
			empty.put("area", area);
			nowHeight += emptyViewHeight;
			ViewGroup child = newElement(renderer, empty, config, root,
					options, true);
			styleDelimiter(child, config);
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
		LinearLayout root = new LinearLayout(element.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setBackgroundResource(R.color.opacity);
		styleGrid(root, config, params);
		element.addView(root, params);
		String area = config.optString("area", "main");
		List<JSONObject> items = renderer.items(area);
		for (int i = 0; i < items.size(); i++) {
			JSONObject itm = items.get(i);
			ViewGroup child = newElement(renderer, itm, config, root, options,
					false);
			if (i > 0) {
				styleDelimiter(child, config);
			}
		}
		JSONObject empty = new JSONObject();
		empty.put("area", area);
		empty.put("sheet_id", item.getLong("id"));
		ViewGroup child = newElement(renderer, empty, config, root, options,
				false);
		if (items.size() > 0) {
			styleDelimiter(child, config);
			// } else {
			// child.setBackgroundResource(R.drawable.delimiter_0);
		}
		element.setTag(R.id.empty_items, new Integer(0));
	}

}
