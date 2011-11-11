package org.kvj.lima1.android.ui.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.util.Log;

public class UIManager {
	
	private static final String TAG = "UIManager";

	public UIManager() {
		protocols.put("@:", new ItemProtocol());
	}

	Map<String, PageProtocol> protocols = new HashMap<String, PageProtocol>();
	Pattern injectPattern = Pattern.compile("\\$\\{([a-z\\@]+\\:)([a-zA-Z0-9\\s\\(\\)\\+\\-\\_/\\:\\.]*)\\}");
	Pattern replacePattern = Pattern.compile("^([a-z\\@]+\\:)([a-zA-Z0-9\\s\\(\\)\\+\\-\\_/\\:\\.]*)$");
	
	public String inject(String text, JSONObject item) {
		Matcher matcher = injectPattern.matcher(text);
		StringBuffer result = new StringBuffer();
//		Log.i(TAG, "Injecting "+text+" to "+item);
		while(matcher.find()) {
//			Log.i(TAG, "Found protocol: "+matcher.group(1)+", "+matcher.group(2));
			PageProtocol p = protocols.get(matcher.group(1));
			if (null != p) {
				String value = p.convert(matcher.group(2), item);
				matcher.appendReplacement(result, value);
			}
		}
		matcher.appendTail(result);
		return result.toString();
	}

	public String replace(String text, JSONObject item) {
		Matcher matcher = replacePattern.matcher(text);
		StringBuffer result = new StringBuffer();
//		Log.i(TAG, "Replacing "+text+" to "+item);
		while(matcher.find()) {
//			Log.i(TAG, "Found protocol: "+matcher.group(1)+", "+matcher.group(2));
			PageProtocol p = protocols.get(matcher.group(1));
			if (null != p) {
				String value = p.convert(matcher.group(2), item);
				matcher.appendReplacement(result, value);
			}
		}
		matcher.appendTail(result);
		return result.toString();
	}

}
