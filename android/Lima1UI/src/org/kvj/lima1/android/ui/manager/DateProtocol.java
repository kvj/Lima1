package org.kvj.lima1.android.ui.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DateProtocol extends PageProtocol {

	private static final String DT_FORMAT = "yyyyMMdd";
	private static final String TAG = "dt:";
	private DateFormat defaultFormat = new SimpleDateFormat(DT_FORMAT);
	Pattern dtPattern = Pattern.compile("^(\\((([ewmdy][+-]?[0-9]+)+)\\))?([EwMdy/\\:\\.]*)$");
	Pattern opPattern = Pattern.compile("([ewmdy])([+-]?)([0-9]+)");
	
	@Override
	public void prepare(JSONObject config, String value) {
		if (!config.has("dt")) {
			Date dt = new Date();
			try {
				dt = defaultFormat.parse(value.substring(value.indexOf(":")+1));
			} catch (ParseException e) {
			}
			try {
				config.put("dt", defaultFormat.format(dt));
			} catch (JSONException e) {
			}
		}
	}
	
	private int getValue(Date dt, String type) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		if ("e".equals(type)) {
			return c.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY;
		}
		if ("w".equals(type)) {
			return c.get(Calendar.WEEK_OF_YEAR);
		}
		if ("d".equals(type)) {
			return c.get(Calendar.DAY_OF_MONTH);
		}
		if ("m".equals(type)) {
			return c.get(Calendar.MONTH);
		}
		if ("y".equals(type)) {
			return c.get(Calendar.YEAR);
		}
		return -1;
	}
	
	private void setValue(Date dt, String type, int value) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		if ("e".equals(type)) {
			c.set(Calendar.DAY_OF_WEEK, value+Calendar.SUNDAY);
		}
		if ("w".equals(type)) {
			c.set(Calendar.WEEK_OF_YEAR, value);
		}
		if ("d".equals(type)) {
			c.set(Calendar.DAY_OF_MONTH, value);
		}
		if ("m".equals(type)) {
			c.set(Calendar.MONTH, value);
		}
		if ("y".equals(type)) {
			c.set(Calendar.YEAR, value);
		}
		dt.setTime(c.getTimeInMillis());
	}
	
	@Override
	public JSONObject accept(JSONObject config, String value) {
		try {
			Date dt = defaultFormat.parse(value);
//			Log.i(TAG, "Accept: "+value+", "+config+", "+dt);
			Iterator<String> it = config.keys();
			while(it.hasNext()) {
				String type = it.next();
				JSONArray valArray = config.optJSONArray(type);
				int val = getValue(dt, type);
				if (-1 != val && null != valArray) {
					boolean found = false;
					for (int i = 0; i < valArray.length(); i++) {
						if (valArray.getInt(i) == val) {
							found = true;
							break;
						}
					}
					if (!found) {
						return null;
					}
				}
			}
			JSONObject result = new JSONObject();
			result.put("dt", defaultFormat.format(dt));
			return result;
		} catch (Exception e) {
			Log.e(TAG, "Error in accept", e);
		}
		return null;
	}
	
	@Override
	public String convert(String text, JSONObject value) {
		try {
			Date dt = defaultFormat.parse(value.getString("dt"));
			Matcher m = dtPattern.matcher(text);
//			Log.i(TAG, "convert: "+text+", "+value);
			if (m.find()) {
				String modifiers = m.group(2);
				if (null == modifiers) {
					modifiers = "";
				}
				String format = DT_FORMAT;
				if (!"".equals(m.group(4))) {
					format = m.group(4);
				}
				Matcher mm = opPattern.matcher(modifiers);
				while (mm.find()) {
					String op = mm.group(1);
					String sign = mm.group(2);
					int val = Integer.parseInt(mm.group(3));
					int cur = getValue(dt, op);
					if (cur != -1) {
						setValue(dt, op, "+".equals(sign)? cur+val: "-".equals(sign)? cur-val: val);
					}
				}
//				Log.i(TAG, "convert2: "+text+", "+format);
				return new SimpleDateFormat(format).format(dt);
			} else {
				Log.w(TAG, "Invalid value: "+text);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error converting", e);
		}
		return text;
	}
}
