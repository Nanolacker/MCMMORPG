package com.mcmmorpg.common.utils;

public class MathUtils {

	private MathUtils() {
		// no instances
	}

	/**
	 * Returns the midpoint between two endpoints.
	 * 
	 * @param endpoint1 the first endpoint
	 * @param endpoint2 the second endpoint
	 * @return the midpoint between two endpoints
	 */
	public static double midpoint(double endpoint1, double endpoint2) {
		return (endpoint1 + endpoint2) / 2.0;
	}

	/**
	 * Clamps a number between a minimum and a maximum.
	 *
	 * @param num the number to be clamped
	 * @param min the minimum value to clamp the number to
	 * @param max the maximum value to clamp the number to
	 */
	public static double clamp(double num, double min, double max) {
		if (!(min <= max)) {
			throw new IllegalArgumentException("Value of min " + "(" + min + ")"
					+ " must be less than or equal to that of max " + "(" + max + ")");
		}
		if (num < min) {
			return min;
		} else if (num > max) {
			return max;
		}
		return num;
	}

	/**
	 * Returns whether a number lies in an interval.
	 * 
	 * @param num              the number to be checked
	 * @param endpoint1        the first endpoint of the interval
	 * @param includeEndpoint1 whether the first endpoint is included in the
	 *                         interval
	 * @param endpoint2        the second endpoint of the interval
	 * @param includeEndpoint2 whether the second endpoint is included in the
	 *                         interval
	 * @return whether the number lies in the interval
	 */
	public static boolean checkInInterval(double num, double endpoint1, boolean includeEndpoint1, double endpoint2,
			boolean includeEndpoint2) {
		if (!(endpoint1 <= endpoint2)) {
			throw new IllegalArgumentException("Value of endpoint1 must be less than or equal to that of endpoint2");
		}
		if (includeEndpoint1) {
			if (includeEndpoint2) {
				return num >= endpoint1 && num <= endpoint2;
			} else {
				return num >= endpoint1 && num < endpoint2;
			}
		} else {
			if (includeEndpoint2) {
				return num > endpoint1 && num <= endpoint2;
			} else {
				return num > endpoint1 && num < endpoint2;
			}
		}
	}

	/**
	 * Rounds a number up to the least multiple of a given factor that is multiplied
	 * by an integer. If the number and factor are equal, the number is returned.
	 * roundUpToLeastMultiple(2, 4) returns 4 because 4 is the least multiple of 4
	 * that is multiplied by an integer (1) that 2 can be rounded up to.
	 * roundUpToLeastMultiple(11, 5) returns 15 because 15 is the least multiple of
	 * 5 that is multiplied by an integer (3) that 11 can be rounded up to.
	 * 
	 * @param num    the number to be rounded up, must be zero or positive
	 * @param factor the factor
	 * @return
	 */
	public static double roundUpToLeastMultiple(double num, double factor) {
		if (num < 0) {
			throw new IllegalArgumentException("num (" + num + ") must be zero or positive");
		}
		int incrementedFactor = 0;
		while (true) {
			double multiple = factor * incrementedFactor;
			if (multiple >= num) {
				return multiple;
			}
			incrementedFactor++;
		}
	}
}
