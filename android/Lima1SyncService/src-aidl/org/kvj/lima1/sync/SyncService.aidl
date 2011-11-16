package org.kvj.lima1.sync;
import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;

interface SyncService {
	
	String message();
	
	PJSONObject create(String app, String stream, in PJSONObject obj);
	PJSONObject update(String app, String stream, in PJSONObject obj);
	PJSONObject remove(String app, String stream, in PJSONObject obj);
	PJSONObject query(String app, String stream, in QueryOperator[] operators);
	String get(String name, String def);
	void set(String name, String value);
}