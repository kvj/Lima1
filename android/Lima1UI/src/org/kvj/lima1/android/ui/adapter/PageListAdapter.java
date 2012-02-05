package org.kvj.lima1.android.ui.adapter;

import org.kvj.lima1.android.ui.PageList;
import org.kvj.lima1.android.ui.R;
import org.kvj.lima1.android.ui.controller.Lima1Controller;
import org.kvj.lima1.sync.PJSONObject;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PageListAdapter implements ListAdapter {

	private PageList pane = null;

	public PageListAdapter(PageList pane) {
		this.pane = pane;
	}

	PJSONObject[] pages = new PJSONObject[0];
	DataSetObserver observer = null;
	private Lima1Controller controller;

	public int getCount() {
		return pages.length;
	}

	public PJSONObject getItem(int arg0) {
		return pages[arg0];
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.page_list_item, parent,
					false);
		}
		PJSONObject page = getItem(position);
		TextView title = (TextView) convertView
				.findViewById(R.id.page_list_item_title);
		title.setText(page.optString("title"));
		return convertView;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isEmpty() {
		return pages.length == 0;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		this.observer = observer;
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		this.observer = null;
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int position) {
		return true;
	}

	public void reload(boolean archived) {
		PJSONObject[] pages = controller.getPages(archived);
		if (null == pages) {
			pane.notifyUser("Error loading pages");
		} else {
			this.pages = pages;
			if (null != observer) {
				observer.onChanged();
			}
		}

	}

	public void setController(Lima1Controller controller) {
		this.controller = controller;
	}

}
