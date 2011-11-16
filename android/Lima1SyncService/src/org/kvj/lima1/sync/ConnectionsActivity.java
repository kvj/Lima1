package org.kvj.lima1.sync;

import org.kvj.lima1.sync.controller.BackgroundSyncService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ConnectionsActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Intent i = new Intent(this, BackgroundSyncService.class);
		startService(i);
        setContentView(R.layout.main);
    }
}