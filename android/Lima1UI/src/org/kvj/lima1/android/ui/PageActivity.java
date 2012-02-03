package org.kvj.lima1.android.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.android.ui.controller.Lima1App;
import org.kvj.lima1.android.ui.controller.Lima1Controller;
import org.kvj.lima1.android.ui.controller.Lima1Service;
import org.kvj.lima1.android.ui.manager.UIManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;

public class PageActivity extends
		SuperActivity<Lima1App, Lima1Controller, Lima1Service> {

	public PageActivity() {
		super(Lima1Service.class);
	}

	private static final String TAG = "UI";

	private JSONObject _loadTemplate(String res, String id) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream(res), "utf-8"));
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
		// startService(new Intent(this, Lima1Service.class));
		// Log.i(TAG, "Service started");
		try {
			ScrollView layout = new ScrollView(this);
			layout.setBackgroundResource(R.color.white_bg);
			setContentView(layout);
			UIManager ui = new UIManager(layout, _loadTemplate("/todo.json",
					"1"), _loadTemplate("/template.json", "2"));
			ui.openLink("dt:20111115", null);
			// Renderer renderer = new Renderer(null, ui, layout, object, new
			// JSONObject(), new JSONObject());
			// renderer.render();
		} catch (Exception e) {
			Log.e(TAG, "Error loading test template", e);
		}
	}

	@Override
	public void onController(Lima1Controller controller) {
		super.onController(controller);
		Log.i(TAG, "Data is available: " + controller.isAvailable());
	}
}
