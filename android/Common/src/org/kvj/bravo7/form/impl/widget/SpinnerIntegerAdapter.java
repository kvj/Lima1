package org.kvj.bravo7.form.impl.widget;

import org.kvj.bravo7.form.ViewBundleAdapter;
import org.kvj.bravo7.form.impl.bundle.IntegerBundleAdapter;

import android.os.Bundle;
import android.widget.Spinner;

public class SpinnerIntegerAdapter extends ViewBundleAdapter<Spinner, Integer> {

	public SpinnerIntegerAdapter(int resID, Integer def) {
		super(new IntegerBundleAdapter(), resID, def);
	}

	@Override
	public Integer getWidgetValue(Bundle bundle) {
		return getView().getSelectedItemPosition();
	}

	@Override
	public void setWidgetValue(Integer value, Bundle bundle) {
		getView().setSelection(value);
	}

}
