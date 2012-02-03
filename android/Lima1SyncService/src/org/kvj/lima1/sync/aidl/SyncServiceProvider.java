package org.kvj.lima1.sync.aidl;

import org.kvj.lima1.sync.Lima1SyncApp;
import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;
import org.kvj.lima1.sync.SyncService;
import org.kvj.lima1.sync.SyncServiceInfo;
import org.kvj.lima1.sync.controller.SyncController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class SyncServiceProvider extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		if (SyncServiceInfo.INTENT.equals(intent.getAction())
				&& null != intent.getExtras()) {
			final String application = intent.getStringExtra("application");
			final SyncController controller = Lima1SyncApp.getInstance()
					.getBean(SyncController.class);
			return new SyncService.Stub() {

				@Override
				public String message() throws RemoteException {
					return "Hi korea!! " + application;
				}

				@Override
				public String get(String name, String def)
						throws RemoteException {
					return null;
				}

				@Override
				public void set(String name, String value)
						throws RemoteException {
				}

				@Override
				public PJSONObject create(String stream, PJSONObject obj)
						throws RemoteException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public PJSONObject update(String stream, PJSONObject obj)
						throws RemoteException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public PJSONObject remove(String stream, PJSONObject obj)
						throws RemoteException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public PJSONObject[] query(String stream,
						QueryOperator[] operators) throws RemoteException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean startSync() throws RemoteException {
					// TODO Auto-generated method stub
					return false;
				}
			};
		}
		return null;
	}

}
