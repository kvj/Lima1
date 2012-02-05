package org.kvj.lima1.sync;
import org.kvj.lima1.sync.PJSONObject;
import org.kvj.lima1.sync.QueryOperator;

interface SyncService {
	
	String message();
	
	PJSONObject create(String stream, in PJSONObject obj);
	PJSONObject update(String stream, in PJSONObject obj);
	PJSONObject remove(String stream, in PJSONObject obj);
	PJSONObject[] query(String stream, in QueryOperator[] operators, String order, String limit);
	boolean startSync();
	String get(String name, String def);
	void set(String name, String value);
}