package org.kvj.bravo7.form.impl.bundle;

import org.kvj.bravo7.form.BundleAdapter;

import android.os.Bundle;

public class IntegerBundleAdapter implements BundleAdapter<Integer> {

	@Override
	public Integer get(Bundle bundle, String name, Integer def) {
		return bundle.getInt(name, def);
	}

	@Override
	public void set(Bundle bundle, String name, Integer value) {
		bundle.putInt(name, value);
	}

}
