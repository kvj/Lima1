package org.kvj.lima1.android.ui.page;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
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

	@Override
	protected void shrink(int step, View view) {
		int added = (Integer) view.getTag(R.id.empty_items);
		if (added > 0) {
			added -= step;
		}
		view.setTag(R.id.empty_items, added);
	}

	private static final String TAG = "ListUI";

	@Override
	protected void fill(int h, Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		String area = config.optString("area", "main");
		int emptyViewHeight = getFullHeight(element.getChildAt(element
				.getChildCount() - 1));
		int height = getHeight(element);
		int added = (int) Math.floor((h - height) / emptyViewHeight);
		// Log.i(TAG,
		// "fill list["+area+"] h: "+h+", added "+added+", empty "+emptyViewHeight+", height "+height+", () "+(added*emptyViewHeight)+", "+element.getTag(R.id.empty_items));
		for (int i = 0; i < added; i++) {
			JSONObject empty = new JSONObject();
			empty.put("area", area);
			newElement(renderer, empty, config, element, options, true);
		}
	}

	private int getHeight(ViewGroup element) {
		MarginLayoutParams params = (MarginLayoutParams) element
				.getLayoutParams();
		int height = params.topMargin + params.bottomMargin;
		for (int i = 0; i < element.getChildCount(); i++) {
			View child = element.getChildAt(i);
			height += getFullHeight(child);
		}
		return height + element.getPaddingTop() + element.getPaddingBottom();
	}

	@Override
	protected int stretch(int step, Renderer renderer, JSONObject config,
			ViewGroup element) throws JSONException {
		int added = (Integer) element.getTag(R.id.empty_items);
		if (step > 0) {
			added += step;
			element.setTag(R.id.empty_items, added);
		}
		int emptyViewHeight = getFullHeight(element.getChildAt(element
				.getChildCount() - 1));
		int height = getHeight(element);
		// Log.i(TAG, "stretch list: height "+height+", "+emptyViewHeight);
		return height + added * emptyViewHeight;
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
		renderer.extendables.add(element);
		// if (options.empty) {
		// int heightAvail = element.getMeasuredHeight();
		// for (int i = 0; i < element.getChildCount(); i++) {
		// heightAvail -= element.getChildAt(i).getMeasuredHeight();
		// }
		// int emptyViewHeight =
		// element.getChildAt(element.getChildCount()-1).getMeasuredHeight();
		// int spaceAdded = 0;
		// View added = null;
		// // Log.i(TAG,
		// "Time to add another element: "+element.getMeasuredHeight()+", "+emptyView.getMeasuredHeight()+" - "+emptyView.getTop()+", "+heightAvail);
		// while (spaceAdded<=heightAvail) {
		// JSONObject empty = new JSONObject();
		// empty.put("area", area);
		// added = newElement(renderer, empty, config, element, options, true);
		// spaceAdded += emptyViewHeight;
		// // Log.i(TAG, "Added empty: "+spaceAdded+", "+heightAvail);
		// }
		// if (null != added) {
		// renderer.lastItems.add(added);
		// }
		// return null;
		// } else {
		// }
	}

}
