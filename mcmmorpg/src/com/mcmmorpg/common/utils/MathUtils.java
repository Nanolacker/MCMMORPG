package com.mcmmorpg.common.utils;

public class MathUtils {

	private MathUtils() {
	}

	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		} else if (num > max) {
			return max;
		}
		return num;
	}

	public static boolean isBetween(double n, double min, boolean includeMin, double max, boolean includeMax) {
		if (includeMin) {
			if (includeMax) {
				return n >= min && n <= max;
			} else {
				return n >= min && n < max;
			}
		} else {
			if (includeMax) {
				return n > min && n <= max;
			} else {
				return n > min && n < max;
			}
		}
	}

	/**
	 * Converts from seconds to ticks, rounding up to the nearest tick.
	 */
	public static int secondsToTicks(double seconds) {
		return (int) Math.floor(seconds / 20);
	}

	public static double ticksToSeconds(int ticks) {
		return ticks * 20.0;
	}

}
