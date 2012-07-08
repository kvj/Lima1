package org.kvj.bravo7.ipc;

import org.kvj.bravo7.ApplicationContext;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class RemotelyBindableService<T, A extends ApplicationContext>
		extends Service {

	private Class<T> controllerClass = null;
	protected T controller = null;

	public RemotelyBindableService(Class<T> controllerClass) {
		super();
		this.controllerClass = controllerClass;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return getStub();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationContext ctx = A.getInstance();
		controller = ctx.getBean(controllerClass);
	}

	abstract public Binder getStub();

}
