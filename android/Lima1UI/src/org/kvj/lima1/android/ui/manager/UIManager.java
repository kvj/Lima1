package org.kvj.lima1.android.ui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.lima1.android.ui.page.Renderer;
import org.kvj.lima1.sync.PJSONObject;

import android.util.Log;
import android.view.ViewGroup;

public class UIManager {

	public static interface UIManagerDataProvider {

		public JSONObject findTemplate(long id);

		public PJSONObject[] getTemplates();

		public PJSONObject[] getNotes(String sheetID);
	}

	private static final String TAG = "UIManager";
	private static final String VALUE_PATTERN = "([a-zA-Z0-9\\s\\(\\)\\+\\-\\_/\\:\\.]*)";

	Map<String, PageProtocol> protocols = new HashMap<String, PageProtocol>();
	// Map<String, JSONObject> templates = new LinkedHashMap<String,
	// JSONObject>();
	ViewGroup root = null;
	UIManagerDataProvider provider = null;
	PJSONObject[] notes = null;

	Pattern injectPattern = Pattern.compile("\\$\\{([a-z\\@]+)\\:"
			+ VALUE_PATTERN + "\\}");
	Pattern replacePattern = Pattern.compile("^([a-z\\@]+)\\:" + VALUE_PATTERN
			+ "$");
	Pattern linkPattern = Pattern.compile("^([a-z\\@]+)\\:" + VALUE_PATTERN
			+ "$");

	public UIManager(ViewGroup root, UIManagerDataProvider provider) {
		this.root = root;
		this.provider = provider;
		protocols.put("@", new ItemProtocol());
		protocols.put("dt", new DateProtocol());
	}

	public String inject(String text, JSONObject item) {
		Matcher matcher = injectPattern.matcher(text);
		StringBuffer result = new StringBuffer();
		// Log.i(TAG, "Injecting "+text+", "+item);
		while (matcher.find()) {
			PageProtocol p = protocols.get(matcher.group(1));
			// Log.i(TAG,
			// "Found protocol: "+matcher.group(1)+", "+matcher.group(2)+", "+p);
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
		// Log.i(TAG, "Replacing "+text+" to "+item);
		while (matcher.find()) {
			// Log.i(TAG,
			// "Found protocol: "+matcher.group(1)+", "+matcher.group(2));
			PageProtocol p = protocols.get(matcher.group(1));
			if (null != p) {
				String value = p.convert(matcher.group(2), item);
				matcher.appendReplacement(result, value);
			}
		}
		matcher.appendTail(result);
		return result.toString();
	}

	protected void render(JSONObject template, JSONObject sheet, int place)
			throws JSONException {
		notes = null;
		Renderer renderer = new Renderer(null, this, root, template, sheet);
		renderer.render();
	}

	public boolean showPage(JSONObject sheet) {
		return showPage(sheet.optLong("template_id"), sheet);
	}

	public boolean showPage(long templateID, JSONObject sheet) {
		JSONObject template = provider.findTemplate(templateID);
		if (null == template) {
			showError("No template found");
			return false;
		}
		try {
			sheet.put("template_id", template);
			JSONObject config = new JSONObject();
			JSONObject protocol = template.optJSONObject("protocol");
			if (null != protocol && sheet.has("code")) {
				Iterator<String> it = protocol.keys();
				while (it.hasNext()) {
					PageProtocol p = protocols.get(it.next());
					if (null != p) {
						p.prepare(sheet, sheet.optString("code"));
					}
				}
			}
			render(template, sheet, 0);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean openSheetByCode(JSONObject object) {
		JSONObject empty = new JSONObject();
		try {
			empty.put("code", object.optString("code"));
			return showPage(object.optLong("template_id"), empty);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void showError(String message) {
		Log.e(TAG, "UI error: " + message);
	}

	public boolean openLink(String link, String place) throws JSONException {
		Log.i(TAG, "Open link: " + link);
		Matcher matcher = linkPattern.matcher(link);
		List<JSONObject> templatesFound = new ArrayList<JSONObject>();
		if (matcher.find()) {
			String name = matcher.group(1);
			PageProtocol p = protocols.get(name);
			if (null == p) {
				Log.w(TAG, "Unsupported tag: " + name);
				return false;
			}
			for (JSONObject tmpl : provider.getTemplates()) {
				JSONObject protocols = tmpl.optJSONObject("protocol");
				// Log.i(TAG, "Template have protocol: "+protocols);
				if (null != protocols && protocols.has(name)
						&& tmpl.has("code")) {
					JSONObject config = p.accept(protocols.optJSONObject(name),
							matcher.group(2));
					// Log.i(TAG, "Got config: "+config);
					if (null == config) {
						continue;
					}
					String code = inject(tmpl.optString("code"), config);
					// Log.i(TAG, "Code: "+code);
					if (null != code) {
						JSONObject obj = new JSONObject();
						obj.put("code", code);
						obj.put("template_id", tmpl.optLong("id"));
						templatesFound.add(obj);
					}
				}
			}
		}
		Log.i(TAG, "Templates found: " + templatesFound.size());
		if (1 == templatesFound.size()) {
			return openSheetByCode(templatesFound.get(0));
		}
		return false;
	}

	public List<JSONObject> getNotes(String sheetID, String area) {
		if (null == notes) {
			notes = provider.getNotes(sheetID);
		}
		List<JSONObject> result = new ArrayList<JSONObject>();
		for (int i = 0; i < notes.length; i++) {
			JSONObject note = notes[i];
			if (null != area && area.equals(note.optString("area"))) {
				result.add(note);
			}
		}
		return result;
	}

}
