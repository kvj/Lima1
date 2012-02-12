package org.kvj.lima1.android.ui.widget;

import org.json.JSONObject;
import org.kvj.bravo7.SuperActivity;
import org.kvj.lima1.android.ui.controller.Lima1Controller;
import org.kvj.lima1.android.ui.manager.EditorInfo;
import org.kvj.lima1.android.ui.manager.UIManager;
import org.kvj.lima1.android.ui.manager.UIManager.UIManagerDataProvider;
import org.kvj.lima1.sync.PJSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class PageFragment extends Fragment implements UIManagerDataProvider {

	private static final String TAG = "Page";
	ScrollView root = null;
	private Lima1Controller controller = null;
	private UIManager manager = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = new ScrollView(getActivity());
		manager = new UIManager(root, this);
		return root;
	}

	public void setController(Lima1Controller controller, long pageID) {
		this.controller = controller;
		JSONObject sheet = controller.findPage(pageID);
		Log.i(TAG, "Showing page: " + pageID + ", " + sheet);
		if (null == sheet) {
			SuperActivity.notifyUser(getActivity(), "Page not found");
		} else {
			if (!manager.showPage(sheet)) {
				SuperActivity.notifyUser(getActivity(), "Error rendering page");
			}
		}
	}

	public JSONObject findTemplate(long id) {
		Log.i(TAG, "Searching template: " + id);
		return controller.findTemplate(id);
	}

	public PJSONObject[] getTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	public ScrollView getRoot() {
		return root;
	}

	public PJSONObject[] getNotes(String sheetID) {
		return controller.getNotes(sheetID);
	}

	public void toggleArchived() {
		manager.toggleArchived();
	}

	public EditorInfo getEditorInfo() {
		return manager.getEditorInfo();
	}

	public void redraw() {
		manager.redraw();
	}
}
