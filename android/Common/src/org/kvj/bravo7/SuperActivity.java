package org.kvj.bravo7;

import java.io.File;

import org.kvj.bravo7.ControllerConnector.ControllerReceiver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class SuperActivity<A extends ApplicationContext, T, S extends SuperService<T, A>> extends Activity implements
		ControllerReceiver<T> {

	Class<S> serviceClass = null;

	public SuperActivity(Class<S> serviceClass) {
		this.serviceClass = serviceClass;
	}

	private static final String TAG = "SuperActivity";
	protected T controller = null;
	ControllerConnector<A, T, S> connector = new ControllerConnector<A, T, S>(this, this);

	public void onController(T controller) {
		this.controller = controller;
	}

	@Override
	protected void onStart() {
		super.onStart();
		connector.connectController(serviceClass);
	}

	@Override
	protected void onStop() {
		super.onStop();
		connector.disconnectController();
	}

	public static void notifyUser(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public void notifyUser(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public static File getExternalCacheFolder(Context context) {
		File dir = null;
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			dir = context.getExternalCacheDir();
		} else {
			dir = Environment.getExternalStorageDirectory();
			if (null != dir) {
				dir = new File(dir, "Android/data/" + context.getPackageName() + "/cache/");
			}
		}
		if (null != dir) {
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					dir = context.getCacheDir();
				}
			}
		}
		return dir;
	}

	public static Bundle getData(Activity activity, Bundle inData) {
		if (null != inData) { // Have data - restore state
			return inData;
		} else { // Don't have - new run or from Intent
			if (activity.getIntent() != null && activity.getIntent().getExtras() != null) {
				// Have data in Intent
				return activity.getIntent().getExtras();
			}
		}
		return new Bundle();
	}

	public static void showQuestionDialog(Context context, String title, String message, final Runnable... buttons) {
		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (buttons.length > 0) { // Have handler
							buttons[0].run();
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (buttons.length > 1) { // Have handler
							buttons[1].run();
						}
					}
				}).show();
	}

	protected ProgressDialog showProgressDialog(String message) {
		return showProgressDialog(this, message);
	}

	public static ProgressDialog showProgressDialog(Context ctx, String message) {
		final ProgressDialog progressDialog = new ProgressDialog(ctx);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		return progressDialog;
	}

}
