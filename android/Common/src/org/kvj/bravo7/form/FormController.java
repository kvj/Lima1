package org.kvj.bravo7.form;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class FormController {

	class Pair {
		WidgetBundleAdapter<?, ?> viewAdapter;
	}

	private static final String TAG = "Form";

	private Map<String, Pair> pairs = new LinkedHashMap<String, Pair>();
	private Map<String, Object> originalValues = new HashMap<String, Object>();
	protected View view;

	public FormController(View view) {
		this.view = view;
	}

	public <V, T> void add(WidgetBundleAdapter<V, T> viewAdapter, String name) {
		Pair pair = new Pair();
		pair.viewAdapter = viewAdapter;
		pairs.put(name, pair);
		viewAdapter.setController(this);
	}

	private void loadDefaultValues(Bundle values) {
		originalValues.clear();
		for (String name : pairs.keySet()) {
			Pair pair = pairs.get(name);
			originalValues.put(name, pair.viewAdapter.get(name, values));
			// Log.i(TAG, "Load origins: " + name + " = " +
			// pair.viewAdapter.get(name, values));
		}
	}

	private void loadValues(Bundle data) {
		if (null != data) {
			for (String name : pairs.keySet()) {
				Pair pair = pairs.get(name);
				pair.viewAdapter.restore(name, data);
				// Log.i(TAG, "Load: " + name + " = " +
				// pair.viewAdapter.get(name, data));
			}
		}
	}

	public void load(DialogFragment dialog, Bundle data) {
		Bundle values = new Bundle();
		if (null != dialog.getArguments()) {
			values = dialog.getArguments();
		}
		loadDefaultValues(values);
		if (null == data) {
			data = values;
		}
		// Set values to views
		loadValues(data);
	}

	public void load(FragmentActivity activity, Bundle data) {
		Bundle values = new Bundle();
		if (null != activity.getIntent() && null != activity.getIntent().getExtras()) {
			values = activity.getIntent().getExtras();
		}
		loadDefaultValues(values);
		if (null == data) {
			data = values;
		}
		// Set values to views
		loadValues(data);
	}

	public void save(Bundle data) {
		for (String name : pairs.keySet()) {
			Pair pair = pairs.get(name);
			pair.viewAdapter.save(name, data);
		}
	}

	public <T> T getValue(String name, Class<T> cl) {
		Pair p = pairs.get(name);
		if (null == p) {
			return null;
		}
		return (T) p.viewAdapter.getWidgetValue();
	}

	public <T extends ViewBundleAdapter<?, ?>> T getAdapter(String name, Class<T> cl) {
		Pair p = pairs.get(name);
		if (null == p) {
			return null;
		}
		return (T) p.viewAdapter;
	}

	public boolean changed() {
		for (String name : pairs.keySet()) {
			Pair pair = pairs.get(name);
			Object value = pair.viewAdapter.getWidgetValue();
			Object orig = originalValues.get(name);
			// Log.i(TAG, "Load: " + name + " = " + orig + " - " + value);
			if (null != value && null != orig && !value.equals(orig)) {
				return true;
			}
		}
		return false;
	}
}
