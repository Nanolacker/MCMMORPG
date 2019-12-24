package com.mcmmorpg.common.time;

public class GameClock {

	private static boolean started;
	private static long startTimeMilis;

	private GameClock() {
		// no instances
	}

	public static void start() {
		if (started) {
			throw new IllegalStateException("Clock already started.");
		}
		started = true;
		startTimeMilis = System.currentTimeMillis();
	}

	/**
	 * Returns the number of seconds that have passed since the starting of the
	 * server.
	 */
	public static double getTime() {
		long currentTimeMilis = System.currentTimeMillis();
		return (currentTimeMilis - startTimeMilis) / 1000.0;
	}

}
