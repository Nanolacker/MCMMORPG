package com.mcmmorpg.common.utils;

import org.bukkit.util.Vector;

/**
 * A direction on a compass.
 */
public enum CardinalDirection {

	EAST("E"), SOUTHEAST("SE"), SOUTH("S"), SOUTHWEST("SW"), WEST("W"), NORTHWEST("NW"), NORTH("N"), NORTHEAST("NE");

	private final String abbreviation;

	private CardinalDirection(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Returns the cardinal direction corresponding to the specified x and z
	 * direction components.
	 */
	public static CardinalDirection forVector(double x, double z) {
		double angle = Math.atan2(z, x);
		int octant = (int) (Math.round((8 * angle / (2 * Math.PI) + 8))) % 8;
		return values()[octant];
	}

	/**
	 * Returns the cardinal direction corresponding to the specified direction
	 * vector.
	 */
	public static CardinalDirection forVector(Vector direction) {
		return forVector(direction.getX(), direction.getZ());
	}

	@Override
	public String toString() {
		return abbreviation;
	}

}
