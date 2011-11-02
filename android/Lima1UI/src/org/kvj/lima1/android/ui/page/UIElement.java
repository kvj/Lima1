package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;

abstract public class UIElement {

	abstract protected View render(Renderer renderer, JSONObject item, 
			JSONObject config, ViewGroup element, 
			UIElementOptions options) throws JSONException;
}
