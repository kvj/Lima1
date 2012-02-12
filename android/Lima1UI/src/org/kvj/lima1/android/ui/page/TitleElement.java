package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;
import org.kvj.lima1.android.ui.manager.EditorInfo;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TitleElement extends UIElement {

	private static final String TAG = "TitleUI";

	@Override
	protected void render(Renderer renderer, JSONObject item,
			JSONObject config, ViewGroup element, UIElementOptions options)
			throws JSONException {
		RelativeLayout layout = new RelativeLayout(element.getContext());
		element.addView(layout);
		TextView title = new TextView(element.getContext());
		title.setId(renderer.getNextID());
		title.setTextSize(15);
		title.setTextColor(0xff000000);
		LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.addView(title, titleParams);
		String property = renderer.replace(config.optString("edit"), item);
		EditText editor = (EditText) LayoutInflater.from(element.getContext())
				.inflate(R.layout.text_item, element, false);
		renderer.setupTextEditor(new EditorInfo(editor, item, options.type,
				property, options.empty));
		LayoutParams editorParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		editorParams.addRule(RelativeLayout.RIGHT_OF, title.getId());
		layout.addView(editor, editorParams);
		title.setText(renderer.inject(config.optString("name"), item));
		editor.setText(item.optString(property, ""));
	}

}
