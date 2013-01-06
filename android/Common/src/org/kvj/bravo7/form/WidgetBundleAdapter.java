package org.kvj.bravo7.form;

import android.os.Bundle;

abstract public class WidgetBundleAdapter<V, T> {

	protected T defaultValue;
	private BundleAdapter<T> adapter;
	protected FormController controller = null;
	protected String key = null;

	public WidgetBundleAdapter(BundleAdapter<T> adapter, T def) {
		this.defaultValue = def;
		this.adapter = adapter;
	}

	public T save(String name, Bundle bundle) {
		T value = getWidgetValue(bundle);
		adapter.set(bundle, name, value);
		return value;
	}

	public T getWidgetValue() {
		return getWidgetValue(null);
	}

	public abstract T getWidgetValue(Bundle bundle);

	public void restore(String name, Bundle bundle) {
		setWidgetValue(get(name, bundle), bundle);
	}

	public void setWidgetValue(T value) {
		setWidgetValue(value, null);
	}

	public abstract void setWidgetValue(T value, Bundle bundle);

	public T get(String name, Bundle data) {
		return adapter.get(data, name, defaultValue);
	}

	public void setController(FormController controller) {
		this.controller = controller;
	}

}
