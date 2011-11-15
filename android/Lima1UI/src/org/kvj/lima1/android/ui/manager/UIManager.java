package org.kvj.lima1.android.ui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.page.Renderer;

import android.util.Log;
import android.view.ViewGroup;

public class UIManager {
	
	private static final String TAG = "UIManager";
	private static final String VALUE_PATTERN = "([a-zA-Z0-9\\s\\(\\)\\+\\-\\_/\\:\\.]*)";

	Map<String, PageProtocol> protocols = new HashMap<String, PageProtocol>();
	Map<String, JSONObject> templates = new LinkedHashMap<String, JSONObject>();
	ViewGroup root = null;
	
	Pattern injectPattern = Pattern.compile("\\$\\{([a-z\\@]+)\\:"+VALUE_PATTERN+"\\}");
	Pattern replacePattern = Pattern.compile("^([a-z\\@]+)\\:"+VALUE_PATTERN+"$");
	Pattern linkPattern = Pattern.compile("^([a-z\\@]+)\\:"+VALUE_PATTERN+"$");
	
	public UIManager(ViewGroup root, JSONObject... templates) {
		this.root = root;
		protocols.put("@", new ItemProtocol());
		protocols.put("dt", new DateProtocol());
		for (int i = 0; i < templates.length; i++) {
			JSONObject tmpl = templates[i];
			this.templates.put(tmpl.optString("id"), tmpl);
		}
	}

	
	public String inject(String text, JSONObject item) {
		Matcher matcher = injectPattern.matcher(text);
		StringBuffer result = new StringBuffer();
//		Log.i(TAG, "Injecting "+text+", "+item);
		while(matcher.find()) {
			PageProtocol p = protocols.get(matcher.group(1));
//			Log.i(TAG, "Found protocol: "+matcher.group(1)+", "+matcher.group(2)+", "+p);
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
	
	protected void render(JSONObject template, JSONObject sheet, int place) throws JSONException {
		Renderer renderer = new Renderer(null, this, root, template, sheet);
		renderer.render();
	}
	
	public boolean showPage(String templateID, JSONObject sheet, int place) throws JSONException {
		JSONObject template = templates.get(templateID);
		if (null == template) {
			showError("No template found");
			return false;
		}
		sheet.put("template_id", template);
		JSONObject config = new JSONObject();
		JSONObject protocol = template.optJSONObject("protocol");
		if (null != protocol && sheet.has("code")) {
			Iterator<String> it = protocol.keys();
			while(it.hasNext()) {
				PageProtocol p = protocols.get(it.next());
				if (null != p) {
					p.prepare(sheet, sheet.optString("code"));
				}
			}
		}
		render(template, sheet, 0);
		return true;
	}
	
	public boolean openSheetByCode(JSONObject object) throws JSONException {
		JSONObject empty = new JSONObject();
		empty.put("code", object.optString("code"));
		return showPage(object.optString("template_id"), empty, 0);
	}
	
	public void showError(String message) {
		Log.e(TAG, "UI error: "+message);
	}
	
	public boolean openLink(String link, String place) throws JSONException {
		Log.i(TAG, "Open link: "+link);
		Matcher matcher = linkPattern.matcher(link);
		List<JSONObject> templatesFound = new ArrayList<JSONObject>();
		if (matcher.find()) {
			String name = matcher.group(1);
			PageProtocol p = protocols.get(name);
			if (null == p) {
				Log.w(TAG, "Unsupported tag: "+name);
				return false;
			}
			for (String id : templates.keySet()) {
				JSONObject tmpl = templates.get(id);
				JSONObject protocols = tmpl.optJSONObject("protocol");
//				Log.i(TAG, "Template have protocol: "+protocols);
				if (null != protocols && protocols.has(name) && tmpl.has("code")) {
					JSONObject config = p.accept(protocols.optJSONObject(name), matcher.group(2));
//					Log.i(TAG, "Got config: "+config);
					if (null == config) {
						continue;
					}
					String code = inject(tmpl.optString("code"), config);
//					Log.i(TAG, "Code: "+code);
					if (null != code) {
						JSONObject obj = new JSONObject();
						obj.put("code", code);
						obj.put("template_id", id);
						templatesFound.add(obj);
					}
				}
			}
		}
		Log.i(TAG, "Templates found: "+templatesFound.size());
		if (1 == templatesFound.size()) {
			return openSheetByCode(templatesFound.get(0));
		}
		return false;
	}

}
