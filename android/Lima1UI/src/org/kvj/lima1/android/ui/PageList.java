package org.kvj.lima1.android.ui;

import org.json.JSONObject;
import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.android.ui.adapter.PageListAdapter;
import org.kvj.lima1.android.ui.controller.Lima1App;
import org.kvj.lima1.android.ui.controller.Lima1Controller;
import org.kvj.lima1.android.ui.controller.Lima1Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PageList extends
		SuperActivity<Lima1App, Lima1Controller, Lima1Service> {

	private ListView list = null;
	private PageListAdapter adapter = null;
	boolean archived = false;

	public PageList() {
		super(Lima1Service.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_list);
		list = (ListView) findViewById(R.id.page_list_list);
		adapter = new PageListAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				itemClick(pos);
			}
		});
	}

	protected void itemClick(int pos) {
		Intent showPage = new Intent(this, PageActivity.class);
		JSONObject sheet = adapter.getItem(pos);
		showPage.putExtra("sheet_id", sheet.optLong("id"));
		startActivity(showPage);
	}

	@Override
	public void onController(Lima1Controller controller) {
		if (null == this.controller) {
			adapter.setController(controller);
		}
		adapter.reload(archived);
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
		case R.id.menu_reload:
			archived = !archived;
			adapter.reload(archived);
			break;
		}
		return true;
	}
}
