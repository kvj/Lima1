package org.kvj.lima1.android.ui.page;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.R;

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
		EditText editor = new EditText(element.getContext());
		editor.setBackgroundResource(R.color.opacity);
		editor.setPadding(2, 0, 0, 0);
		editor.setTextSize(15);
		editor.setTextColor(0xff000000);
		LayoutParams editorParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		editorParams.addRule(RelativeLayout.RIGHT_OF, title.getId());
		layout.addView(editor, editorParams);
		title.setText(renderer.inject(config.optString("name"), item));
		editor.setText(renderer.replace(config.optString("edit"), item));
	}

}
