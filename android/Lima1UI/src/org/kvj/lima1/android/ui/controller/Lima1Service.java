package org.kvj.lima1.android.ui.controller;

import org.kvj.bravo7.SuperService;

import android.content.Intent;
import android.util.Log;

public class Lima1Service extends SuperService<Lima1Controller, Lima1App> {

	private static final String TAG = "Lima1Service";

	public Lima1Service() {
		super(Lima1Controller.class, "Lima1");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service created");
		Lima1App.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Start command");
		return super.onStartCommand(intent, flags, startId);
	}

}
