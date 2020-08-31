package com.mcmmorpg.common.time;

/**
 * Keeps track of the time.
 */
public class Clock {

	private static final double UPDATE_PERIOD = 0.05;

	private static double time;

	private Clock() {
		// no instances
	}

	public static void start() {
		time = 0;
		RepeatingTask updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				time += UPDATE_PERIOD;
			}
		};
		updateTask.schedule();
	}

	/**
	 * Returns the number of seconds that have passed since the server started.
	 */
	public static double getTime() {
		return time;
	}

}
