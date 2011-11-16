package org.kvj.bravo7;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SuperService<T> extends Service{

	protected T controller = null;
	private final IBinder binder = new LocalBinder();
	private Notification notification = null;
	protected String title = "Application";
    private static final int SERVICE_NOTIFY = 100;
    protected int notificationID = SERVICE_NOTIFY;
	
	public class LocalBinder extends Binder {
		
		public T getController() {
			return controller;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notification = new Notification();
	}
	
	public void raiseNotification(int icon, String text, Class<? extends Activity> received) {
		notification.icon = icon;
		notification.setLatestEventInfo(getApplicationContext(), title, text, 
				PendingIntent.getActivity(getApplicationContext(), 0, 
						new Intent(getApplicationContext(), received), 
						PendingIntent.FLAG_CANCEL_CURRENT));
		startForeground(notificationID, notification);
	}
	
	public void hideNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(notificationID);
        stopForeground(true);
	}
	
	@Override
	public void onDestroy() {
		hideNotification();
		super.onDestroy();
	}
}
