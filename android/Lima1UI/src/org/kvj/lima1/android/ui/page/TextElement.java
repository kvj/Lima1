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
	protected View render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
//		Log.i(TAG, "Create text: "+element);
		if (options.empty) {
			return null;
		}
		TextView editor = options.disabled? new TextView(element.getContext()): new EditText(element.getContext());
		editor.setBackgroundResource(R.drawable.title_edit_bg);
		editor.setPadding(0, 0, 0, 0);
		editor.setTextSize(12);
		editor.setTextColor(0xffffffff);
		LayoutParams editorParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		element.addView(editor, editorParams);
		editor.setText(renderer.inject(config.optString("edit"), item));
		return null;
	}

}
