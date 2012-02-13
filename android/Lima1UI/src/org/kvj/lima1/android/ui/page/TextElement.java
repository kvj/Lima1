package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;
import org.kvj.lima1.android.ui.manager.EditorInfo;

import android.view.LayoutInflater;
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

		LayoutParams editorParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		TextView editor = null;
		String property = renderer.replace(config.optString("edit"), item);
		if (options.disabled) {
			editor = (TextView) LayoutInflater.from(element.getContext())
					.inflate(R.layout.text_item_readonly, element, false);
		} else {
			EditText eeditor = (EditText) LayoutInflater.from(
					element.getContext()).inflate(R.layout.text_item, element,
					false);
			editor = eeditor;
			renderer.setupTextEditor(new EditorInfo(eeditor, item,
					options.type, property, options.empty));
		}
		element.addView(editor, editorParams);
		editor.setText(item.optString(property, ""));
	}
}
