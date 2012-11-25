package org.kvj.bravo7.form.impl.bundle;

import org.kvj.bravo7.form.BundleAdapter;

import android.os.Bundle;

public class StringBundleAdapter implements BundleAdapter<String> {

	@Override
	public String get(Bundle bundle, String name, String def) {
		String value = bundle.getString(name);
		if (null == value) {
			return def;
		}
		return value;
	}

	@Override
	public void set(Bundle bundle, String name, String value) {
		bundle.putString(name, value);
	}

}
