package org.kvj.lima1.android.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.kvj.lima1.android.ui.manager.UIManager;
import org.kvj.lima1.android.ui.page.Renderer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PageActivity extends Activity {

	private static final String TAG = "UI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream("/todo.json"), 
							"utf-8"));
			StringBuilder buffer = new StringBuilder();
			String line = null;
			while (null != (line = reader.readLine())) {
				buffer.append(line);
			}
			reader.close();
			JSONObject object = new JSONObject(buffer.toString());
			Log.i(TAG, "JSON template ready: "+object.toString());
			UIManager ui = new UIManager();
			ScrollView layout = new ScrollView(this);
			layout.setBackgroundResource(R.color.white_bg);
			setContentView(layout);
			Renderer renderer = new Renderer(null, ui, layout, object, new JSONObject(), new JSONObject());
			renderer.render();
		} catch (Exception e) {
			Log.e(TAG, "Error loading test template", e);
		}
	}
}
