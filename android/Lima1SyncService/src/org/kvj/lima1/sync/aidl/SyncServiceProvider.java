package org.kvj.lima1.sync.aidl;

import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;
import org.kvj.lima1.sync.SyncService;
import org.kvj.lima1.sync.SyncServiceInfo;
import org.kvj.lima1.sync.SyncService.Stub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class SyncServiceProvider extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		if (SyncServiceInfo.INTENT.equals(intent.getAction())) {
			return new SyncService.Stub() {
				
				@Override
				public String message() throws RemoteException {
					return "Hi korea!";
				}

				@Override
				public PJSONObject create(String app, String stream,
						PJSONObject obj) throws RemoteException {
					return obj;
				}

				@Override
				public PJSONObject update(String app, String stream,
						PJSONObject obj) throws RemoteException {
					return null;
				}

				@Override
				public PJSONObject remove(String app, String stream,
						PJSONObject obj) throws RemoteException {
					return null;
				}

				@Override
				public PJSONObject query(String app, String stream,
						QueryOperator[] operators) throws RemoteException {
					return null;
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
			};
		}
		return null;
	}

}
