package org.kvj.bravo7.util;

import java.util.Timer;
import java.util.TimerTask;

public class DelayedTask {

	private long msec;
	private Runnable task;
	private final Timer timer = new Timer();
	private TimerTask timerTask = null;

	public DelayedTask(long msec, Runnable task) {
		this.msec = msec;
		this.task = task;
	}

	public boolean cancel() {
		synchronized (timer) { // Lock
			if (null != timerTask) { // Exist
				boolean result = timerTask.cancel();
				timerTask = null;
				return result;
			}
		}
		return false;
	}

	public void schedule() {
		cancel();
		synchronized (timer) { // Lock
			timerTask = new TimerTask() {

				@Override
				public void run() {
					synchronized (timer) { // Lock
						timerTask = null;
					}
					task.run();
				}
			};
			timer.schedule(timerTask, msec);
		}
	}

}
