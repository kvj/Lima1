package org.kvj.lima1.android.ui.manager;

import org.json.JSONObject;

import android.widget.EditText;

public class EditorInfo {

	public EditText editor;
	public JSONObject object;
	public String type;
	public String field;
	public boolean newEntry;

	public EditorInfo(EditText editor, JSONObject object, String type,
			final String field, boolean newEntry) {
		this.editor = editor;
		this.object = object;
		this.type = type;
		this.field = field;
		this.newEntry = newEntry;
	}
}
