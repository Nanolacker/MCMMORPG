package com.mcmmorpg.common.physics;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.time.RepeatingTask;

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

	/**
	 * Create a new projectile. Note that fire() must be called before it does
	 * anything.
	 */
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

	/**
	 * Fire the projectile to see if it collides with anything.
	 */
	public void fire() {
		if (fired) {
			throw new IllegalStateException("Projectile already fired");
		}
		fired = true;
		updatePosition = new RepeatingTask(UPDATE_POSITION_PERIOD) {
			@Override
			protected void run() {
				Location newLocation = location.clone().add(velocity.clone().multiply(UPDATE_POSITION_PERIOD));
				if (newLocation.distance(start) >= maxDistance) {
					remove();
				} else {
					setLocation(newLocation);
				}
			}
		};
		updatePosition.schedule();
		collider.setActive(true);
	}

	/**
	 * Returns the velocity of this projectile.
	 */
	public final Vector getVelocity() {
		return velocity;
	}

	/**
	 * Set the velocity of this projectile.
	 */
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	/**
	 * Return how far this projectile will go at a maximum (i.e. if it isn't
	 * removed).
	 */
	public final double getMaxDistance() {
		return maxDistance;
	}

	/**
	 * Set how far this projectile will go.
	 */
	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	/**
	 * Returns how large this projectile is (the length of the underlying collider
	 * of this projectile).
	 */
	public final double getHitSize() {
		return hitSize;
	}

	/**
	 * Sets how large this projectile is (the length of the underlying collider of
	 * this projectile).
	 */
	public void setHitSize(double hitSize) {
		this.hitSize = hitSize;
		collider.setDimensions(hitSize, hitSize, hitSize);
	}

	/**
	 * Returns the current location of this projectile. This changes while it is in
	 * motion.
	 */
	public final Location getLocation() {
		return location;
	}

	/**
	 * Set the current location of this projectile. Override this method to add
	 * additional affects. Must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void setLocation(Location location) {
		this.location = location;
		collider.setCenter(location);
	}

	/**
	 * Returns whether this projectile has been fired (i.e. fire() has been called).
	 */
	public final boolean isFired() {
		return fired;
	}

	/**
	 * Make the underlying collider visible (probably for debugging purposes).
	 */
	public void setVisible(boolean visible) {
		collider.setVisible(visible);
	}

	/**
	 * Override this to add collision functionality.
	 */
	protected void onHit(Collider hit) {
	}

	/**
	 * Remove this projectile. An exception will be thrown if the projectile has not
	 * been fired yet. This method is automatically called when this projectile
	 * reaches its maximum distance.
	 */
	@OverridingMethodsMustInvokeSuper
	public void remove() {
		if (fired) {
			// checks if this method has been called before--if so, ignore this call
			if (updatePosition.isScheduled()) {
				collider.setActive(false);
				updatePosition.cancel();
			}
		} else {
			throw new IllegalStateException("Projectile not yet fired");
		}
	}

}
