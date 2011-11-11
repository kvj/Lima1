package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TextElement extends UIElement {

	private static final String TAG = "TextUI";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		TextView editor = options.disabled? new TextView(element.getContext()): new EditText(element.getContext());
		editor.setBackgroundResource(R.color.opacity);
		editor.setPadding(0, 0, 0, 0);
		editor.setTextSize(12);
		editor.setTextColor(0xff222222);
		LayoutParams editorParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		element.addView(editor, editorParams);
		editor.setText(renderer.replace(config.optString("edit"), item));
	}

}
