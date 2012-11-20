package org.kvj.lima1.sync.controller.data;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {
	public List<String> texts = new ArrayList<String>();
	public List<String> numbers = new ArrayList<String>();
	public List<FKey> fkeys = new ArrayList<FKey>();
	List<List<String>> indexes = new ArrayList<List<String>>();
}
