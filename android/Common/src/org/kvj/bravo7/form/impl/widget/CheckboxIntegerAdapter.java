package org.kvj.bravo7.form.impl.widget;

import org.kvj.bravo7.form.ViewBundleAdapter;
import org.kvj.bravo7.form.impl.bundle.BooleanBundleAdapter;

import android.os.Bundle;
import android.widget.CheckBox;

public class CheckboxIntegerAdapter extends ViewBundleAdapter<CheckBox, Boolean> {

	public CheckboxIntegerAdapter(int resID, Boolean def) {
		super(new BooleanBundleAdapter(), resID, def);
	}

	@Override
	public Boolean getWidgetValue(Bundle bundle) {
		return getView().isChecked();
	}

	@Override
	public void setWidgetValue(Boolean value, Bundle bundle) {
		getView().setChecked(value);
	}

}
