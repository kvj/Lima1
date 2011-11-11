package org.kvj.lima1.android.ui.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;
import org.kvj.lima1.android.ui.manager.UIManager;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Renderer {

	private enum RenderState {Initial, Growing, Shrinking};
	
	private static final String TAG = "Renderer";
	protected UIManager ui;
	protected JSONObject env;
	protected JSONObject data;
	protected ViewGroup root;
	protected JSONObject template;
	protected int nextID = 0;
	protected LinearLayout pageLayout = null;
	protected List<View> extendables = new ArrayList<View>();
	protected RenderState renderState = RenderState.Initial;
	
	private static Map<String, UIElement> elements = new HashMap<String, UIElement>();
	
	public Renderer(Object manager, UIManager ui, ViewGroup root, JSONObject template, JSONObject data, JSONObject env) {
		this.ui = ui;
		this.env = env;
		this.data = data;
		this.root = root;
		this.template = template;
	}
	
	protected UIElement get(String name) {
		if (null == name) {
			return elements.get("simple");
		}
		UIElement element = elements.get(name);
		if (null == element) {
			element = elements.get("simple");
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
				Log.i(TAG, "Layout changed: "+changed+", l: "+l+", t: "+t+", r: "+r+", b: "+b);
//				Log.i(TAG, "Render: "+root.getHeight()+" - "+pageLayout.getHeight()+", "+getMeasuredHeight());
				if (changed) {
					UIElementOptions options = new UIElementOptions();
					options.empty = true;
					try {
						int h = pageLayout.getMeasuredHeight();
						while(h<root.getHeight()) {
							h = get(null).stretch(1, Renderer.this, template, pageLayout);
//							Log.i(TAG, "After stretch: "+h);
						}
						while(h>root.getHeight()) {
							for (int i = extendables.size()-1; i >=0 && h>root.getHeight(); i--) {
								get("list").shrink(1, extendables.get(i));
								h = get(null).stretch(0, Renderer.this, template, pageLayout);
//								Log.i(TAG, "After shrink["+i+"]: "+h);
							}
						}
						get(null).fill(h, Renderer.this, data, template, pageLayout, options);
						Log.i(TAG, "Resize done");
					} catch (Exception e) {
						Log.e(TAG, "Error adding: ", e);
					}
				}
			}
		};
		extendables.clear();
		pageLayout.setBackgroundResource(R.color.white_bg);
		pageLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		root.addView(pageLayout, pageLayoutParams);
		UIElementOptions options = new UIElementOptions();
		options.firstPass = true;
		get(null).render(this, data, template, pageLayout, options);
	}
	
	static {
		elements.put("simple", new SimpleElement());
		elements.put("title", new TitleElement());
		elements.put("text", new TextElement());
		elements.put("cols", new ColsElement());
		elements.put("title1", new Title1Element());
		elements.put("list", new ListElement());
	}

	public void applyDefaults(JSONObject jsonObject, JSONObject item) throws JSONException {
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
		// TODO Auto-generated method stub
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		JSONObject testObject = new JSONObject();
		testObject.put("text", "Hi korea!");
		result.add(testObject);
		return result;
	}

	public String replace(String text, JSONObject item) {
		return ui.replace(text, item);
	}
}
