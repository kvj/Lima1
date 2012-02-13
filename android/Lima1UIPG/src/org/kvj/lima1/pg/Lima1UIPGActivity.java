package org.kvj.lima1.pg;

import android.os.Bundle;

import com.phonegap.DroidGap;

public class Lima1UIPGActivity extends DroidGap {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keepRunning = true;
		super.loadUrl("file:///android_asset/client/m.html");
	}
}