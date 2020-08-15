package com.mcmmorpg.common.util;

import org.bukkit.util.Vector;

public class MathUtility {

	public static final Vector LEFT_VECTOR = new Vector(-1, 0, 0);
	public static final Vector DOWN_VECTOR = new Vector(0, -1, 0);
	public static final Vector BACK_VECTOR = new Vector(0, 0, -1);
	public static final Vector RIGHT_VECTOR = new Vector(1, 0, 0);
	public static final Vector UP_VECTOR = new Vector(0, 1, 0);
	public static final Vector FORWARD_VECTOR = new Vector(0, 0, 1);

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
