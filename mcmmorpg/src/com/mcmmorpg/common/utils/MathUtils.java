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

	public static boolean isBetween(double num, double min, boolean includeMin, double max, boolean includeMax) {
		if (includeMin) {
			if (includeMax) {
				return num >= min && num <= max;
			} else {
				return num >= min && num < max;
			}
		} else {
			if (includeMax) {
				return num > min && num <= max;
			} else {
				return num > min && num < max;
			}
		}
	}

	/**
	 * Converts from seconds to ticks, rounding up to the nearest tick.
	 */
	public static int secondsToTicks(double seconds) {
		return (int) Math.round(seconds * 20.0);
	}

	public static double ticksToSeconds(int ticks) {
		return ticks / 20.0;
	}

}
