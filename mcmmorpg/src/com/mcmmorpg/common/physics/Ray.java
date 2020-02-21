package com.mcmmorpg.common.physics;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class Ray {

	private static final Particle DEFAULT_DRAW_PARTICLE = Particle.CRIT;
	private static final double DRAW_THICKNESS = 4.0;

	private Location start;
	private Location end;
	private Particle drawParticle;

	public Ray(Location start, Location end) {
		this.start = start;
		this.end = end;
		drawParticle = DEFAULT_DRAW_PARTICLE;
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

	public void draw() {
		World world = start.getWorld();
		Vector v = end.clone().subtract(start).toVector();
		double length = v.length();
		Vector direction = v.normalize();
		int particleCount = (int) (length * DRAW_THICKNESS);
		Location particleLocation = start.clone();
		double spaceDistance = 1 / DRAW_THICKNESS;
		Vector increment = direction.multiply(spaceDistance);
		for (int i = 0; i < particleCount; i++) {
			world.spawnParticle(drawParticle, particleLocation, 0);
			particleLocation.add(increment);
		}
	}

	public final Particle getDrawParticle() {
		return drawParticle;
	}

	public final void setDrawParticle(Particle particle) {
		drawParticle = particle;
	}

}
