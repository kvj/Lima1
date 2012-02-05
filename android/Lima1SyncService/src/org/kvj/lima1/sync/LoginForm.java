package org.kvj.lima1.sync;

import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.sync.controller.BackgroundSyncService;
import org.kvj.lima1.sync.controller.SyncController;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LoginForm extends
		SuperActivity<Lima1SyncApp, SyncController, BackgroundSyncService> {

	public LoginForm() {
		super(BackgroundSyncService.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}

	private void save() {
		final ProgressDialog progress = showProgressDialog("Checking...");
		final String username = ((TextView) findViewById(R.id.login_username))
				.getText().toString();
		final String password = ((TextView) findViewById(R.id.login_password))
				.getText().toString();
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return controller.verifyToken(username, password);
			}

			protected void onPostExecute(String result) {
				progress.dismiss();
				if (null != result) {
					notifyUser(result);
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
		return true;
	}

}
