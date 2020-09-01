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

	/**
	 * Starts the clock at time = 0. This is only used by the MMORPGPlugin class.
	 */
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
	 * Returns how many seconds have passed since the MMORPG plugin was enabled.
	 */
	public static double getTime() {
		return time;
	}

}
