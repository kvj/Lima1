package org.kvj.lima1.android.ui.controller;

import org.kvj.bravo7.ApplicationContext;

public class Lima1App extends ApplicationContext {

	@Override
	protected void init() {
		publishBean(new Lima1Controller());
	}

}
