package com.mcmmorpg.common.physics;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * A finite ray that can be used for raycasting.
 */
public final class Ray {

	private Location start;
	private Location end;

	/**
	 * Create a new ray with the specified start and end.
	 */
	public Ray(Location start, Location end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Create a new ray with the specified start, direciton, and length.
	 */
	public Ray(Location start, Vector direction, double length) {
		this(start, start.clone().add(direction.multiply(length)));
	}

	/**
	 * Returns the start location of this ray.
	 */
	public Location getStart() {
		return start;
	}

	/**
	 * Sets the start location of this ray.
	 */
	public void setStart(Location start) {
		this.start = start;
	}

	/**
	 * Returns the end location of this ray.
	 */
	public Location getEnd() {
		return end;
	}

	/**
	 * Sets the end location of this ray.
	 */
	public void setEnd(Location end) {
		this.end = end;
	}

	/**
	 * Returns the direction of this ray.
	 */
	public Vector getDirection() {
		return end.clone().subtract(start).toVector().normalize();
	}

	/**
	 * Returns the length of this ray.
	 */
	public double getLength() {
		return end.distance(start);
	}

	/**
	 * Returns whether this ray intersects the specified collider.
	 */
	public boolean intersects(Collider collider) {
		RayTraceResult result = collider.toBoundingBox().rayTrace(start.toVector(), getDirection(), getLength());
		return result != null;
	}

	/**
	 * Draw this ray using particles. Useful for effects or debugging.
	 */
	public void draw(Particle particle, double particleDensity) {
		World world = start.getWorld();
		Vector v = end.clone().subtract(start).toVector();
		double length = v.length();
		Vector direction = v.normalize();
		int particleCount = (int) (length * particleDensity);
		Location particleLocation = start.clone();
		double spaceDistance = 1 / particleDensity;
		Vector increment = direction.multiply(spaceDistance);
		for (int i = 0; i < particleCount; i++) {
			world.spawnParticle(particle, particleLocation, 0);
			particleLocation.add(increment);
		}
	}

}
