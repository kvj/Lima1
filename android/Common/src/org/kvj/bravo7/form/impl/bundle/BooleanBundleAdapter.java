package org.kvj.bravo7.form.impl.bundle;

import org.kvj.bravo7.form.BundleAdapter;

import android.os.Bundle;

public class BooleanBundleAdapter implements BundleAdapter<Boolean> {

	@Override
	public Boolean get(Bundle bundle, String name, Boolean def) {
		return bundle.getBoolean(name, def);
	}

	@Override
	public void set(Bundle bundle, String name, Boolean value) {
		bundle.putBoolean(name, value);
	}

}
