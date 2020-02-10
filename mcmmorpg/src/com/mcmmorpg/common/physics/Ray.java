package com.mcmmorpg.common.physics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.Debug;

public final class Ray {

	private static final Particle DEFAULT_DRAW_PARTICLE = Particle.CRIT;
	private static final double DRAW_PERIOD = 0.1;
	private static final double DRAW_THICKNESS = 4.0;

	private Location start;
	private Location end;
	private boolean drawingEnabled;
	private Particle drawParticle;
	private RepeatingTask drawTask;

	public Ray(Location start, Location end) {
		this.start = start;
		this.end = end;
		drawingEnabled = false;
		drawParticle = DEFAULT_DRAW_PARTICLE;
		drawTask = null;
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

	public boolean intersects(Collider collider) {
		Vector v = end.clone().subtract(start).toVector();
		RayTraceResult result = collider.toBoundingBox().rayTrace(start.toVector(), v.clone().normalize(), v.length());
		return result != null;
	}

	public void setDrawingEnabled(boolean enabled) {
		boolean redundant = this.drawingEnabled == enabled;
		if (redundant) {
			return;
		}
		this.drawingEnabled = enabled;
		if (drawingEnabled) {
			if (drawTask == null) {
				drawTask = new RepeatingTask(DRAW_PERIOD) {
					@Override
					protected void run() {
						draw();
					}
				};
			}
			drawTask.schedule();
			String startDesc = String.format(ChatColor.YELLOW + "start = (%.1f, %.1f, %.1f)", start.getX(),
					start.getY(), start.getZ());
			Debug.log(ChatColor.WHITE + "Drawing of ray has been enabled. (" + startDesc + ChatColor.WHITE + ")");
		} else {
			drawTask.cancel();
		}
	}

	private void draw() {
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
