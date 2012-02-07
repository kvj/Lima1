package org.kvj.lima1.android.ui;

import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.android.ui.controller.Lima1App;
import org.kvj.lima1.android.ui.controller.Lima1Controller;
import org.kvj.lima1.android.ui.controller.Lima1Service;
import org.kvj.lima1.android.ui.widget.PageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PageActivity extends
		SuperActivity<Lima1App, Lima1Controller, Lima1Service> {

	public PageActivity() {
		super(Lima1Service.class);
	}

	private static final String TAG = "UI";

	PageFragment page0 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_viewer);
		page0 = (PageFragment) getFragmentManager().findFragmentById(
				R.id.page_0);
		try {
			// UIManager ui = new UIManager(layout, _loadTemplate("/todo.json",
			// "1"), _loadTemplate("/template.json", "2"));
			// ui.openLink("dt:20111115", null);
			// Renderer renderer = new Renderer(null, ui, layout, object, new
			// JSONObject(), new JSONObject());
			// renderer.render();
		} catch (Exception e) {
			Log.e(TAG, "Error loading test template", e);
		}
	}

	@Override
	public void onController(Lima1Controller controller) {
		if (null == this.controller) {
			Log.i(TAG,
					"Show page size: " + page0.getView().getHeight() + ", "
							+ page0.getRoot().getHeight() + ", "
							+ findViewById(R.id.page_viewer_root).getHeight());
			page0.setController(controller,
					getIntent().getLongExtra("sheet_id", 0));
		}
		super.onController(controller);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sync:
			String result = controller.sync();
			if (null != result) {
				notifyUser(result);
			}
			break;
		}
		return true;
	}
}
