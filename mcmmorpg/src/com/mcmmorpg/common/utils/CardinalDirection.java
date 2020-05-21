package com.mcmmorpg.common.utils;

import org.bukkit.util.Vector;

public enum CardinalDirection {

	EAST("E"), SOUTHEAST("SE"), SOUTH("S"), SOUTHWEST("SW"), WEST("W"), NORTHWEST("NW"), NORTH("N"), NORTHEAST("NE");

	private final String abbreviation;

	private CardinalDirection(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public static CardinalDirection forVector(double x, double z) {
		double angle = Math.atan2(z, x);
		int octant = (int) (Math.round((8 * angle / (2 * Math.PI) + 8))) % 8;
		return values()[octant];
	}

	public static CardinalDirection forVector(Vector vector) {
		return forVector(vector.getX(), vector.getZ());
	}

	@Override
	public String toString() {
		return abbreviation;
	}

}
