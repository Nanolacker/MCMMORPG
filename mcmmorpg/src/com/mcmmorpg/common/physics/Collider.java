package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.util.MathUtility;

/**
 * Represents an axis-aligned box collider.
 */
public class Collider {

	/**
	 * Whether this collider will collide and respond to other colliders.
	 */
	private boolean active;
	/**
	 * The world that this collider exists in.
	 */
	private World world;
	/**
	 * Represents the bounds of this collider.
	 */
	private double xMin, yMin, zMin, xMax, yMax, zMax;
	/**
	 * The collider buckets that this collider occupies.
	 */
	private List<ColliderBucket> occupiedBuckets;
	/**
	 * The colliders that this collider is currently colliding with.
	 */
	private List<Collider> collidingColliders;

	/**
	 * Constructs a new axis-aligned, cuboid collider. The max value on any axis
	 * must be greater than that axis's min value. Be sure to invoke
	 * {@code setActive} to activate this collider after construction.
	 * 
	 * @param world
	 *            the world this collider will exist in
	 * @param xMin
	 *            the minimum x value that exists within this collider
	 * @param xMax
	 *            the maximum x value that exists within this collider
	 * @param yMin
	 *            the minimum y value that exists within this collider
	 * @param yMax
	 *            the maximum y value that exists within this collider
	 * @param zMin
	 *            the minimum z value that exists within this collider
	 * @param zMax
	 *            the maximum z value that exists within this collider
	 */
	public Collider(World world, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		this.world = world;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;

		active = false;
		occupiedBuckets = new ArrayList<ColliderBucket>();
		collidingColliders = new ArrayList<Collider>();
	}

	/**
	 * Constructs a new {@code Collider} from the specified {@link BoundingBox}.
	 * 
	 * @param world
	 *            the world this collider will exist in
	 * @param boundingBox
	 *            the collider used to construct this {@code Collider}
	 */
	public Collider(World world, BoundingBox boundingBox) {
		this(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(),
				boundingBox.getMaxY(), boundingBox.getMaxZ());
	}

	/**
	 * Constructs a new axis-aligned collider. Any length of this collider should
	 * not be negative. Be sure to invoke {@code setActive} to activate collider
	 * after construction.
	 * 
	 * @param center
	 *            the location, including the world, at the center of this collider
	 * @param lengthX
	 *            the length of this collider on the x-axis
	 * @param lengthY
	 *            the length of this collider on the y-axis
	 * @param lengthZ
	 *            the length of this collider on the z-axis
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the lengths are negative
	 */
	public Collider(Location center, double lengthX, double lengthY, double lengthZ) {
		this(center.getWorld(), center.getX() - lengthX / 2.0, center.getY() - lengthY / 2.0,
				center.getZ() - lengthZ / 2.0, center.getX() + lengthX / 2.0, center.getY() + lengthY / 2.0,
				center.getZ() + lengthZ / 2.0);
	}

	/**
	 * Returns whether this collider will interact with other colliders.
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * Sets whether this collider will interact with other colliders.
	 */
	public final void setActive(boolean active) {
		this.active = active;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		} else {
			// need to copy to prevent comodification in handleCollisionExit()
			List<Collider> collidingCollidersCopy = new ArrayList<>(collidingColliders);
			for (Collider collidingWith : collidingCollidersCopy) {
				handleCollisionExit(collidingWith);
			}
			for (ColliderBucket bucket : occupiedBuckets) {
				bucket.removeCollider(this);
			}
			occupiedBuckets.clear();
		}
	}

	/**
	 * Returns the world this collider exists in.
	 */
	public final World getWorld() {
		return world;
	}

	/**
	 * Sets the world that this collider will exist in.
	 * 
	 * @param world
	 *            the world this collider will exist in
	 */
	public final void setWorld(World world) {
		this.world = world;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum x value that exists inside this collider.
	 */
	public final double getXMin() {
		return xMin;
	}

	public final void setXMin(double xMin) {
		this.xMin = xMin;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public final double getYMin() {
		return yMin;
	}

	public final void setYMin(double yMin) {
		this.yMin = yMin;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public final double getZMin() {
		return zMin;
	}

	public final void setZMin(double zMin) {
		this.zMin = zMin;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the maximum x value that exists inside this collider.
	 */
	public final double getXMax() {
		return xMax;
	}

	public final void setXMax(double xMax) {
		this.xMax = xMax;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public final double getYMax() {
		return yMax;
	}

	public final void setYMax(double yMax) {
		this.yMax = yMax;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public final double getZMax() {
		return zMax;
	}

	public final void setZMax(double zMax) {
		this.zMax = zMax;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the location of the point that exists at the center of this bounding
	 * box.
	 */
	public final Location getCenter() {
		double x = (xMin + xMax) / 2.0;
		double y = (yMin + yMax) / 2.0;
		double z = (zMin + zMax) / 2.0;
		Location center = new Location(world, x, y, z);
		return center;
	}

	/**
	 * Sets the center of this collider and updates all bounds accordingly.
	 * 
	 * @param center
	 *            the new center of this collider
	 */
	public final void setCenter(Location center) {
		world = center.getWorld();
		updateBounds(center);
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	private final void updateBounds(Location newCenter) {
		double xMid = newCenter.getX();
		double halfLengthX = getLengthX() / 2.0;
		xMin = xMid - halfLengthX;
		xMax = xMid + halfLengthX;
		double yMid = newCenter.getY();
		double halfLengthY = getLengthY() / 2.0;
		yMin = yMid - halfLengthY;
		yMax = yMid + halfLengthY;
		double zMid = newCenter.getZ();
		double halfLengthZ = getLengthZ() / 2.0;
		zMin = zMid - halfLengthZ;
		zMax = zMid + halfLengthZ;
	}

	/**
	 * Translates this collider. Translating this collider so that it overlaps with
	 * another colliders will result in {@link Collider#onCollisionEnter} being
	 * called for each collider. Conversely, translating this collider so that it
	 * does not overlap with any colliders that it previously overlapped with will
	 * result in {@link Collider#onCollisionExit} being called for each collider.
	 */
	public final void translate(double x, double y, double z) {
		xMin += x;
		xMax += x;
		yMin += y;
		yMax += y;
		zMin += z;
		zMax += z;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the length of this collider on the x-axis.
	 */
	public final double getLengthX() {
		return xMax - xMin;
	}

	/**
	 * Returns the length of this collider on the y-axis.
	 */
	public final double getLengthY() {
		return yMax - yMin;
	}

	/**
	 * Returns the length of this collider on the z-axis.
	 */
	public final double getLengthZ() {
		return zMax - zMin;
	}

	/**
	 * Sets the dimensions of the collider.
	 * 
	 * @param dimensions
	 *            the new dimensions of this collider
	 */
	public final void setDimensions(double lengthX, double lengthY, double lengthZ) {
		updateBounds(lengthX, lengthY, lengthZ);
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	public final BoundingBox toBoundingBox() {
		return new BoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
	}

	private final void updateBounds(double newLengthX, double newLengthY, double newLengthZ) {
		double xMid = (xMin + xMax) / 2.0;
		double halfLengthX = newLengthX / 2.0;
		xMin = xMid - halfLengthX;
		xMax = xMid + halfLengthX;
		double yMid = (yMin + yMax) / 2.0;
		double halfLengthY = newLengthY / 2.0;
		yMin = yMid - halfLengthY;
		yMax = yMid + halfLengthY;
		double zMid = (zMin + zMax) / 2.0;
		double halfLengthZ = newLengthZ / 2.0;
		zMin = zMid - halfLengthZ;
		zMax = zMid + halfLengthZ;
	}

	/**
	 * Determines what collider buckets this collider should exist in to ensure
	 * efficient and accurate collision detection. Bounds must be current to update
	 * properly.
	 */
	private final void updateOccupiedBuckets() {
		List<ColliderBucket> occupiedBucketsOld = new ArrayList<ColliderBucket>(occupiedBuckets);
		occupiedBuckets.clear();
		int bucketSize = ColliderBucket.BUCKET_SIZE;

		int bucketXMin = (int) (xMin / bucketSize);
		int bucketYMin = (int) (yMin / bucketSize);
		int bucketZMin = (int) (zMin / bucketSize);

		int bucketXMax = (int) (xMax / bucketSize);
		int bucketYMax = (int) (yMax / bucketSize);
		int bucketZMax = (int) (zMax / bucketSize);

		for (int xCount = bucketXMin; xCount <= bucketXMax; xCount++) {
			for (int yCount = bucketYMin; yCount <= bucketYMax; yCount++) {
				for (int zCount = bucketZMin; zCount <= bucketZMax; zCount++) {
					Location bucketAddress = new Location(world, xCount, yCount, zCount);
					ColliderBucket bucket = ColliderBucket.forAddress(bucketAddress);
					if (bucket == null) {
						bucket = ColliderBucket.createNewBucket(bucketAddress);
					}
					boolean alreadyEncompassed = bucket.getActiveColliders().contains(this);
					if (!alreadyEncompassed) {
						bucket.encompassCollider(this);
					}
					occupiedBuckets.add(bucket);
				}
			}
		}

		for (int i = 0; i < occupiedBucketsOld.size(); i++) {
			ColliderBucket bucket = occupiedBucketsOld.get(i);
			if (!occupiedBuckets.contains(bucket)) {
				bucket.removeCollider(this);
				if (bucket.getActiveColliders().isEmpty()) {
					Location address = bucket.getAddress();
					ColliderBucket.deleteBucket(address);
				}
			}
		}
	}

	/**
	 * Returns whether this collider encompasses a point (in other words, whether
	 * this point exists within the volume of this collider).
	 * 
	 * @param point
	 *            location of the point to be checked
	 * @return whether this collider encompasses the point
	 */
	public final boolean encompasses(Location point) {
		World world = point.getWorld();
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
		return world.equals(this.world) && MathUtility.isBetween(x, xMin, true, xMax, true)
				&& MathUtility.isBetween(y, yMin, true, yMax, true) && MathUtility.isBetween(z, zMin, true, zMax, true);
	}

	/**
	 * Detects the presence and absence of collisions between this collider and
	 * other colliders and responds appropriately.
	 */
	private final void checkForCollision() {
		for (int i = 0; i < occupiedBuckets.size(); i++) {
			ColliderBucket bucket = occupiedBuckets.get(i);
			List<Collider> neighboringColliders = new ArrayList<>(bucket.getActiveColliders());
			for (int j = 0; j < neighboringColliders.size(); j++) {
				Collider neighboringCollider = neighboringColliders.get(j);
				if (neighboringCollider != this) {
					boolean collides = isCollidingWith(neighboringCollider);
					if (collidingColliders.contains(neighboringCollider)) {
						if (!collides) {
							handleCollisionExit(neighboringCollider);
						}
					} else {
						if (collides) {
							handleCollisionEnter(neighboringCollider);
						}
					}
				}
			}
		}
		outerloop: for (int i = 0; i < collidingColliders.size(); i++) {
			Collider collidingCollider = collidingColliders.get(i);
			for (int j = 0; j < occupiedBuckets.size(); j++) {
				ColliderBucket bucket = occupiedBuckets.get(j);
				if (bucket.getActiveColliders().contains(collidingCollider)) {
					continue outerloop;
				}
			}
			handleCollisionExit(collidingCollider);
		}
	}

	/**
	 * Responds to this collider and another collider colliding with each other.
	 * Called when two colliders that were colliding no longer overlap each other.
	 * {@code onCollisionEnter} is called from each of the bounding boxes.
	 * 
	 * @param other
	 *            the other collider in the collision
	 */
	private final void handleCollisionEnter(Collider other) {
		this.collidingColliders.add(other);
		other.collidingColliders.add(this);
		this.onCollisionEnter(other);
		other.onCollisionEnter(this);
	}

	/**
	 * Responds to this collider and another collider retracting from a collision.
	 * Called when two colliders that were colliding no longer overlap each other.
	 * {@code onCollisionExit} is called from each of the colliders.
	 * 
	 * @param other
	 *            the other collider in the collision
	 */
	private final void handleCollisionExit(Collider other) {
		collidingColliders.remove(other);
		other.collidingColliders.remove(this);
		this.onCollisionExit(other);
		other.onCollisionExit(this);
	}

	/**
	 * Returns whether this {@code Collider} is colliding with the specified
	 * {@code Collider}.
	 * 
	 * @param other
	 *            the other {@code Collider}
	 * @return whether the two {@code Collider}s are colliding
	 */
	public final boolean isCollidingWith(Collider other) {
		return (this.getXMin() <= other.getXMax() && this.getXMax() >= other.getXMin())
				&& (this.getYMin() <= other.getYMax() && this.getYMax() >= other.getYMin())
				&& (this.getZMin() <= other.getZMax() && this.getZMax() >= other.getZMin());
	}

	/**
	 * Returns an array of colliders which this collider is currently colliding
	 * with.
	 */
	public final Collider[] getCollidingColliders() {
		return collidingColliders.toArray(new Collider[collidingColliders.size()]);
	}

	/**
	 * Called when this collider enters a collision with another collider.
	 * 
	 * @param other
	 *            the other collider in the collision
	 */
	protected void onCollisionEnter(Collider other) {
	}

	/**
	 * Called when this collider exits a collision with another collider.
	 * 
	 * @param other
	 *            the other collider in the collision
	 */
	protected void onCollisionExit(Collider other) {
	}

}
