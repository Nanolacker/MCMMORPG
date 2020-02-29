package com.mcmmorpg.common.physics;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class Ray {

	private Location start;
	private Location end;

	public Ray(Location start, Location end) {
		this.start = start;
		this.end = end;
	}

	public Ray(Location start, Vector direction, double distance) {
		this(start, start.clone().add(direction.multiply(distance)));
	}

	public Location getStart() {
		return start;
	}

	public void setStart(Location start) {
		this.start = start;
	}

	public Location getEnd() {
		return end;
	}

	public void setEnd(Location end) {
		this.end = end;
	}

	public Vector getDirection() {
		return end.clone().subtract(start).toVector().normalize();
	}

	public double getLength() {
		return end.distance(start);
	}

	public boolean intersects(Collider collider) {
		RayTraceResult result = collider.toBoundingBox().rayTrace(start.toVector(), getDirection(), getLength());
		return result != null;
	}

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
