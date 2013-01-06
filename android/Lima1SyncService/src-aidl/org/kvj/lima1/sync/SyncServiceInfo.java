package org.kvj.lima1.sync;

public class SyncServiceInfo {
	public static final String INTENT = "org.kvj.lima1.sync.SyncService";
	public static final String STARTED_INTENT = "org.kvj.lima1.sync.SyncService.START";
	public static final String DESTROYED_INTENT = "org.kvj.lima1.sync.SyncService.DESTROY";
	public static final String SYNC_START_INTENT = "org.kvj.lima1.sync.SyncService.SYNC_START";
	public static final String SYNC_FINISH_INTENT = "org.kvj.lima1.sync.SyncService.SYNC_FINISH";
	public static final String PACKAGE = "org.kvj.lima1.sync";
	public static final String SERVICE = ".SyncServiceProvider";

	public static final String KEY_APP = "application";
	public static final String KEY_RESULT = "result";
	public static final String KEY_TOKEN = "token";
}
