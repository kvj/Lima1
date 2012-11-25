package org.kvj.bravo7.form.impl.widget;

import org.kvj.bravo7.form.ViewBundleAdapter;
import org.kvj.bravo7.form.impl.bundle.StringBundleAdapter;

import android.os.Bundle;
import android.widget.TextView;

public class TextViewStringAdapter extends ViewBundleAdapter<TextView, String> {

	public TextViewStringAdapter(int resID, String def) {
		super(new StringBundleAdapter(), resID, def);
	}

	@Override
	public String getWidgetValue(Bundle bundle) {
		return getView().getText().toString().trim();
	}

	@Override
	public void setWidgetValue(String value, Bundle bundle) {
		getView().setText(value);
	}

}
