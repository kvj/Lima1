package org.kvj.bravo7.form.impl.widget;

import org.kvj.bravo7.form.BundleAdapter;
import org.kvj.bravo7.form.WidgetBundleAdapter;

import android.os.Bundle;

public class TransientAdapter<T> extends WidgetBundleAdapter<T, T> {

	private T value;

	public TransientAdapter(BundleAdapter<T> adapter, T def) {
		super(adapter, def);
	}

	@Override
	public T getWidgetValue(Bundle bundle) {
		return value;
	}

	@Override
	public void setWidgetValue(T value, Bundle bundle) {
		this.value = value;
	}

}
