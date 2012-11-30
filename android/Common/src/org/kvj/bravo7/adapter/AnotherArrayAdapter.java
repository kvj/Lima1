package org.kvj.bravo7.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

public abstract class AnotherArrayAdapter<T> extends BaseAdapter {

	private T[] data;
	private int resourceID;

	public AnotherArrayAdapter(T[] data, int resourceID) {
		this.data = data;
		this.resourceID = resourceID;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public T getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(resourceID, parent, false);
		customize(convertView, position);
		return convertView;
	}

	abstract public void customize(View view, int position);

	public boolean setValue(Spinner spinner, T value) {
		for (int i = 0; i < getCount(); i++) {
			T item = getItem(i);
			if (item.equals(value)) {
				spinner.setSelection(i);
				return true;
			}
		}
		return false;
	}
}
