package org.kvj.lima1.sync;

import org.kvj.bravo7.ControllerConnector;
import org.kvj.bravo7.ControllerConnector.ControllerReceiver;
import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.sync.controller.BackgroundSyncService;
import org.kvj.lima1.sync.controller.SyncController;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LoginForm extends SherlockActivity implements ControllerReceiver<SyncController> {

	private SyncController controller = null;
	ControllerConnector<Lima1SyncApp, SyncController, BackgroundSyncService> cc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
	}

	@Override
	protected void onStart() {
		super.onStart();
		cc = new ControllerConnector<Lima1SyncApp, SyncController, BackgroundSyncService>(this, this);
		cc.connectController(BackgroundSyncService.class);
	}

	@Override
	protected void onStop() {
		super.onStop();
		cc.disconnectController();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}

	private void save() {
		final ProgressDialog progress = SuperActivity.showProgressDialog(this, "Checking...");
		final String username = ((TextView) findViewById(R.id.login_username)).getText().toString();
		final String password = ((TextView) findViewById(R.id.login_password)).getText().toString();
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return controller.verifyToken(username, password);
			}

			@Override
			protected void onPostExecute(String result) {
				progress.dismiss();
				if (null != result) {
					SuperActivity.notifyUser(getApplicationContext(), result);
				} else {
					finish();
				}
			};

		}.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_login_save:
			save();
			break;
		}
		return false;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.menu_login_save:
	// save();
	// break;
	// }
	// return true;
	// }
	//
	@Override
	public void onController(SyncController controller) {
		this.controller = controller;
	}

}
