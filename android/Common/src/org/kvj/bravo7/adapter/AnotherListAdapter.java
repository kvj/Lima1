package org.kvj.bravo7.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AnotherListAdapter<T> extends BaseAdapter {
	private List<T> data;
	private int resourceID;

	public AnotherListAdapter(List<T> data, int resourceID) {
		this.data = data;
		this.resourceID = resourceID;
	}

	public void setData(List<T> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(resourceID, parent, false);
		customize(convertView, position);
		return convertView;
	}

	abstract public void customize(View view, int position);

}
