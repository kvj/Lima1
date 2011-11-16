package org.kvj.lima1.android.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.kvj.lima1.android.ui.manager.UIManager;
import org.kvj.lima1.android.ui.page.Renderer;
import org.kvj.lima1.sync.SyncServiceConnection;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PageActivity extends Activity {

	private static final String TAG = "UI";
	
	private SyncServiceConnection sync = new SyncServiceConnection() {
		
		public void onConnected() {
			try {
				Log.i(TAG, "Sync service connected: "+connection.message());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		};
		
		public void onDisconnected() {
			Log.i(TAG, "Sync service disconnected");
		};
	};

	private JSONObject _loadTemplate(String res, String id) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(res), 
							"utf-8"));
			StringBuilder buffer = new StringBuilder();
			String line = null;
			while (null != (line = reader.readLine())) {
				buffer.append(line);
			}
			reader.close();
			JSONObject object = new JSONObject(buffer.toString());
			object.put("id", id);
			return object;
		} catch (Exception e) {
			Log.e(TAG, "Error reading", e);
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ScrollView layout = new ScrollView(this);
			layout.setBackgroundResource(R.color.white_bg);
			setContentView(layout);
			UIManager ui = new UIManager(layout, _loadTemplate("/todo.json", "1"), _loadTemplate("/template.json", "2"));
			ui.openLink("dt:20111115", null);
//			Renderer renderer = new Renderer(null, ui, layout, object, new JSONObject(), new JSONObject());
//			renderer.render();
		} catch (Exception e) {
			Log.e(TAG, "Error loading test template", e);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		sync.connect(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		sync.disconnect(this);
	}
}
