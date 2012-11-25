package org.kvj.bravo7.form;

import android.os.Bundle;

public interface BundleAdapter<T> {
	public T get(Bundle bundle, String name, T def);

	public void set(Bundle bundle, String name, T value);
}
