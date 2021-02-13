package com.mcmmorpg.common.util;

import org.bukkit.util.Vector;

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
        } else {
            return num;
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
        return ticks * 0.05;
    }

    /**
     * Linearly interpolates between a and b by t.
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static Vector lerp(Vector a, Vector b, double t) {
        return new Vector(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
    }
}
