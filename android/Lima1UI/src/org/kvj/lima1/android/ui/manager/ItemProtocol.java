package org.kvj.lima1.android.ui.manager;

import org.json.JSONObject;

public class ItemProtocol extends PageProtocol {

	@Override
	public String convert(String text, JSONObject value) {
		return value.optString(text, "");
	}
}
