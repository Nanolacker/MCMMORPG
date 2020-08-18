package com.mcmmorpg.common.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ParticleEffects {

	public static void line(Particle particle, double particleDensity, Location start, Vector direction,
			double length) {
		World world = start.getWorld();
		direction = direction.clone();
		if (!direction.isNormalized()) {
			direction.normalize();
		}
		int particleCount = (int) (length * particleDensity);
		Location particleLocation = start.clone();
		double spaceDistance = 1.0 / particleDensity;
		Vector increment = direction.multiply(spaceDistance);
		for (int i = 0; i < particleCount; i++) {
			world.spawnParticle(particle, particleLocation, 0);
			particleLocation.add(increment);
		}
	}

	public static void line(Particle particle, double particleDensity, Location start, Location end) {
		Vector v = end.clone().subtract(start).toVector();
		double length = v.length();
		Vector direction = v.normalize();
		line(particle, particleDensity, start, direction, length);
	}

	public static void filledBox(Particle particle, double particleDensity, Location min, Location max) {
		double spaceDistance = 1.0 / particleDensity;
		World world = min.getWorld();
		for (double xCount = min.getX(); xCount <= max.getX(); xCount += spaceDistance) {
			for (double yCount = min.getY(); yCount <= max.getY(); yCount += spaceDistance) {
				for (double zCount = min.getZ(); zCount <= max.getZ(); zCount += spaceDistance) {
					Location point = new Location(world, xCount, yCount, zCount);
					world.spawnParticle(particle, point, 0);
				}
			}
		}
	}

	public static void filledBox(Particle particle, double particleDensity, Location center, double sizeX, double sizeY,
			double sizeZ) {
		double semiLengthX = sizeX / 2.0;
		double semiLengthY = sizeY / 2.0;
		double semiLengthZ = sizeZ / 2.0;
		Location min = center.clone().subtract(semiLengthX, semiLengthY, semiLengthZ);
		Location max = center.clone().add(semiLengthX, semiLengthY, semiLengthZ);
		filledBox(particle, particleDensity, min, max);
	}

	public static void wireframeBox(Particle particle, double particleDensity, Location min, Location max) {
		double spaceDistance = 1.0 / particleDensity;
		World world = min.getWorld();
		// represents whether xCount has reached reached its maximum
		boolean xFinished = false;
		for (double xCount = min.getX(); xCount <= max.getX() && !xFinished; xCount += spaceDistance) {
			// represents whether yCount has reached reached its maximum
			boolean yFinished = false;
			for (double yCount = min.getY(); yCount <= max.getY() && !yFinished; yCount += spaceDistance) {
				// represents whether zCount has reached reached its maximum
				boolean zFinished = false;
				for (double zCount = min.getZ(); zCount <= max.getZ() && !zFinished; zCount += spaceDistance) {
					int validCount = 0;
					if (xCount == min.getX()) {
						validCount++;
					}
					if (xCount > max.getX() - spaceDistance) {
						validCount++;
						xFinished = true;
					}
					if (yCount == min.getY()) {
						validCount++;
					}
					if (yCount > max.getY() - spaceDistance) {
						validCount++;
						yFinished = true;
					}
					if (zCount == min.getZ()) {
						validCount++;
					}
					if (zCount > max.getZ() - spaceDistance) {
						validCount++;
						zFinished = true;
					}
					boolean validPoint = validCount >= 2;
					if (validPoint) {
						Location point = new Location(world, xCount, yCount, zCount);
						world.spawnParticle(particle, point, 0);
					}
				}
			}
		}
	}

	public static void wireframeBox(Particle particle, double particleDensity, Location center, double sizeX,
			double sizeY, double sizeZ) {
		double semiLengthX = sizeX / 2.0;
		double semiLengthY = sizeY / 2.0;
		double semiLengthZ = sizeZ / 2.0;
		Location min = center.clone().subtract(semiLengthX, semiLengthY, semiLengthZ);
		Location max = center.clone().add(semiLengthX, semiLengthY, semiLengthZ);
		wireframeBox(particle, particleDensity, min, max);
	}

	public static void sphere(Particle particle, double particleDensity, Location center, double radius) {
		double spaceDistance = 1.0 / particleDensity;
		World world = center.getWorld();
		double radiusSquared = radius * radius;
		Location boxMin = center.clone().subtract(radius, radius, radius);
		Location boxMax = center.clone().add(radius, radius, radius);
		for (double xCount = boxMin.getX(); xCount <= boxMax.getX(); xCount += spaceDistance) {
			for (double yCount = boxMin.getY(); yCount <= boxMax.getY(); yCount += spaceDistance) {
				for (double zCount = boxMin.getZ(); zCount <= boxMax.getZ(); zCount += spaceDistance) {
					Location point = new Location(world, xCount, yCount, zCount);
					if (point.distanceSquared(center) <= radiusSquared) {
						world.spawnParticle(particle, point, 0);
					}
				}
			}
		}
	}

	public static void circle(Particle particle, double particleDensity, Location center, double radius) {

	}

	public static void cone() {

	}

	public static void spiral() {

	}

}
