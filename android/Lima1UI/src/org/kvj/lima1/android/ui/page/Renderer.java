package org.kvj.lima1.android.ui.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

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
	protected List<View> lastItems = new ArrayList<View>();
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
		return ui.inject(text, item, env);
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
				Log.i(TAG, "Render: "+root.getHeight()+" - "+pageLayout.getHeight()+", "+getMeasuredHeight());
				if (changed) {
					UIElementOptions options = new UIElementOptions();
					options.empty = true;
					try {
						while(pageLayout.getMeasuredHeight()<root.getHeight()) {
							lastItems.clear();
							get(null).render(Renderer.this, data, template, pageLayout, options);
							measure(root.getWidth(), root.getHeight());
							//layout(l, t, r, getMeasuredHeight());
							Log.i(TAG, "After fix: "+root.getHeight()+" - :"+pageLayout.getHeight()+", "+getMeasuredHeight());
						}
						while(pageLayout.getMeasuredHeight()>root.getHeight() && !lastItems.isEmpty()) {
							View toRemove = lastItems.get(lastItems.size()-1);
							((ViewGroup)toRemove.getParent()).removeView(toRemove);
							lastItems.remove(lastItems.size()-1);
							measure(root.getWidth(), root.getHeight());
							Log.i(TAG, "After shrink: "+root.getHeight()+" - :"+pageLayout.getHeight()+", "+getMeasuredHeight());
						}
					} catch (Exception e) {
						Log.e(TAG, "Error adding: ", e);
					}
				}
			}
		};
//		pageLayout.setBackgroundResource(R.drawable.col_bg);
		pageLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		root.addView(pageLayout, pageLayoutParams);
		UIElementOptions options = new UIElementOptions();
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

	public void applyDefaults(JSONObject jsonObject, JSONObject item) {
		// TODO Auto-generated method stub
		
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
}
