package org.kvj.lima1.android.ui.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;
import org.kvj.lima1.android.ui.manager.UIManager;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Renderer {

	private enum RenderState {
		Initial, Growing, Shrinking
	};

	private static final String TAG = "Renderer";
	private static final double SQRT_2 = 1.4142;
	protected UIManager ui;
	protected JSONObject data;
	protected ViewGroup root;
	protected JSONObject template;
	protected int nextID = 0;
	protected LinearLayout pageLayout = null;
	protected RenderState renderState = RenderState.Initial;

	private static Map<String, UIElement> elements = new HashMap<String, UIElement>();
	private static SimpleElement simpleElement;

	public Renderer(Object manager, UIManager ui, ViewGroup root,
			JSONObject template, JSONObject data) {
		this.ui = ui;
		this.data = data;
		this.root = root;
		this.template = template;
	}

	protected UIElement get(String name) {
		if (null == name) {
			return simpleElement;
		}
		UIElement element = elements.get(name);
		if (null == element) {
			element = simpleElement;
		}
		return element;
	}

	protected String inject(String text, JSONObject item) {
		return ui.inject(text, item);
	}

	public void render() throws JSONException {
		root.removeAllViews();
		renderState = RenderState.Initial;
		pageLayout = new LinearLayout(root.getContext()) {
			@Override
			protected void onLayout(boolean changed, int l, int t, int r, int b) {
				super.onLayout(changed, l, t, r, b);
				if (renderState != RenderState.Initial) {
					return;
				}
				renderState = RenderState.Growing;
				if (changed) {
					UIElementOptions options = new UIElementOptions();
					options.empty = true;
					try {
						int rootWidth = root.getMeasuredWidth();
						int rootHeight = root.getMeasuredHeight();
						if (rootHeight < SQRT_2 * rootWidth) {
							rootHeight = (int) (SQRT_2 * rootWidth);
						}
						int h = pageLayout.getMeasuredHeight();
						if (h < rootHeight) {
							get(null).grow(rootHeight, Renderer.this, template,
									pageLayout, options);
						}
						// Log.i(TAG, "Resize done");
					} catch (Exception e) {
						Log.e(TAG, "Error adding: ", e);
					}
				}
			}
		};
		pageLayout.setBackgroundResource(R.color.white_bg);
		pageLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		root.addView(pageLayout, pageLayoutParams);
		UIElementOptions options = new UIElementOptions();
		options.firstPass = true;
		get(null).render(this, data, template, pageLayout, options);
	}

	static {
		simpleElement = new SimpleElement();
		elements.put("simple", simpleElement);
		elements.put("title", new TitleElement());
		elements.put("text", new TextElement());
		elements.put("cols", new ColsElement());
		elements.put("title1", new Title1Element());
		elements.put("list", new ListElement());
	}

	public void applyDefaults(JSONObject jsonObject, JSONObject item)
			throws JSONException {
		for (Iterator<String> it = item.keys(); it.hasNext();) {
			String key = it.next();
			if (!item.has(key)) {
				item.put(key, inject(jsonObject.getString(key), item));
			}
		}

	}

	public int getNextID() {
		return ++nextID;
	}

	protected List<JSONObject> items(String area) throws JSONException {
		return ui.getNotes(data.optString("id"), area);
	}

	public String replace(String text, JSONObject item) {
		return ui.replace(text, item);
	}
}
