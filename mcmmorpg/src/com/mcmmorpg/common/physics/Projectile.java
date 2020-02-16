package com.mcmmorpg.common.physics;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.Debug;

/**
 * This can be used for projectiles such as fireballs and arrows.
 */
public class Projectile {

	private static final double UPDATE_POSITION_PERIOD = 0.2;

	private final Location start;
	private Vector velocity;
	private double maxDistance;
	private double hitSize;
	private Location location;
	private final Collider collider;
	private RepeatingTask updatePosition;
	private boolean fired;

	public Projectile(Location start, Vector velocity, double maxDistance, double hitSize) {
		this.start = start;
		this.velocity = velocity;
		this.maxDistance = maxDistance;
		this.hitSize = hitSize;
		this.location = start.clone();
		collider = new Collider(start, hitSize, hitSize, hitSize) {
			@Override
			protected void onCollisionEnter(Collider other) {
				onHit(other);
			}
		};
		fired = false;
	}

	public final void fire() {
		if (fired) {
			throw new IllegalStateException("Projectile already fired");
		}
		fired = true;
		collider.setActive(true);
		updatePosition = new RepeatingTask(UPDATE_POSITION_PERIOD) {
			@Override
			protected void run() {
				Location newLocation = location.clone().add(velocity.clone().multiply(UPDATE_POSITION_PERIOD));
				if (newLocation.distance(start) >= maxDistance) {
					Debug.log("cancelled");
					cancel();
				} else {
					setLocation(newLocation);
				}
			}
		};
		updatePosition.schedule();
	}

	public final Vector getVelocity() {
		return velocity;
	}

	public final void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public final double getMaxDistance() {
		return maxDistance;
	}

	public final void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public final double getHitSize() {
		return hitSize;
	}

	public final void setHitSize(double hitSize) {
		this.hitSize = hitSize;
		collider.setDimensions(hitSize, hitSize, hitSize);
	}

	public final Location getLocation() {
		return location;
	}

	@OverridingMethodsMustInvokeSuper
	protected void setLocation(Location location) {
		this.location = location;
		collider.setCenter(location);
	}

	public final boolean isFired() {
		return fired;
	}

	protected void onHit(Collider hit) {
	}

	@OverridingMethodsMustInvokeSuper
	public void remove() {
		if (fired) {
			collider.setActive(false);
			updatePosition.cancel();
		} else {
			throw new IllegalStateException("Projectile not being fired");
		}
	}

}
