package org.kvj.lima1.sync.aidl;

import org.kvj.lima1.sync.Lima1SyncApp;
import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;
import org.kvj.lima1.sync.SyncService;
import org.kvj.lima1.sync.SyncServiceInfo;
import org.kvj.lima1.sync.controller.SyncController;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

public class SyncServiceProvider extends Service {

	Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		if (SyncServiceInfo.INTENT.equals(intent.getAction()) && null != intent.getExtras()) {
			final String application = intent.getStringExtra("application");
			final SyncController controller = Lima1SyncApp.getInstance().getBean(SyncController.class);
			return new SyncService.Stub() {

				@Override
				public String message() throws RemoteException {
					return "Hi korea!! " + application;
				}

				@Override
				public String get(String name, String def) throws RemoteException {
					return null;
				}

				@Override
				public void set(String name, String value) throws RemoteException {
				}

				@Override
				public PJSONObject create(String stream, PJSONObject obj) throws RemoteException {
					return controller.createUpdate(application, stream, obj);
				}

				@Override
				public PJSONObject update(String stream, PJSONObject obj) throws RemoteException {
					return controller.createUpdate(application, stream, obj);
				}

				@Override
				public PJSONObject remove(String stream, PJSONObject obj) throws RemoteException {
					return controller.remove(application, stream, obj);
				}

				@Override
				public PJSONObject[] query(String stream, QueryOperator[] operators, String order, String limit)
						throws RemoteException {
					return controller.query(application, stream, operators, order, limit);
				}

				@Override
				public PJSONObject removeCascade(String stream, PJSONObject obj) throws RemoteException {
					// TODO: Implement removeCascade
					return controller.remove(application, stream, obj);
				}

				@Override
				public String sync() throws RemoteException {
					return controller.sync(application);
				}

				@Override
				public String getFile(String name) throws RemoteException {
					return controller.getFile(application, name);
				}

				@Override
				public boolean removeFile(String name) throws RemoteException {
					return controller.removeFile(application, name);
				}

				@Override
				public String uploadFile(String path) throws RemoteException {
					return controller.uploadFile(application, path);
				}
			};
		}
		return null;
	}

}
