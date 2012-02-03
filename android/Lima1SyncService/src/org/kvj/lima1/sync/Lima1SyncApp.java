package org.kvj.lima1.sync;

import org.kvj.bravo7.ApplicationContext;
import org.kvj.lima1.sync.controller.SyncController;

public class Lima1SyncApp extends ApplicationContext {

	@Override
	protected void init() {
		publishBean(new SyncController());
	}

}
