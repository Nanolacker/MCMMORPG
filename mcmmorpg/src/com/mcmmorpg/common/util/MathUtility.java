package com.mcmmorpg.common.util;

public class MathUtility {

	private MathUtility() {
	}

	/**
	 * Returns num restricted between min and max (inclusive).
	 */
	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		} else if (num > max) {
			return max;
		}
		return num;
	}

	/**
	 * Returns whether num is between the two endpoints.
	 */
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
	 * Converts seconds approximately (rounded) to ticks.
	 */
	public static int secondsToTicks(double seconds) {
		return (int) Math.round(seconds * 20.0);
	}

	/**
	 * Converts from ticks to seconds exactly.
	 */
	public static double ticksToSeconds(int ticks) {
		return ticks / 20.0;
	}

	/**
	 * Linearly interpolates between a and b by t.
	 */
	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

}
