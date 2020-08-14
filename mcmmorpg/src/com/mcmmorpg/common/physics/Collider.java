package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.util.MathUtility;

/**
 * Represents an axis-aligned box collider. Instances of this class should
 * override the onCollisionEnter() and onCollisionExit() methods to add
 * collision behavior. Colliders will not interact with other colliders until
 * setActive() has been called.
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
	 * Represents the lower and upper bounds of this collider.
	 */
	private double minX, minY, minZ, maxX, maxY, maxZ;
	/**
	 * The collider buckets that this collider occupies.
	 */
	private final List<ColliderBucket> occupiedBuckets;
	/**
	 * The colliders that this collider is currently contacting.
	 */
	private final List<Collider> contacts;

	/**
	 * Constructs a new collider from the specified lower and upper bounds. This
	 * constructor assumes that all max bounds are greater than their corresponding
	 * min bounds.
	 */
	public Collider(World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.world = world;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		active = false;
		occupiedBuckets = new ArrayList<ColliderBucket>();
		contacts = new ArrayList<Collider>();
	}

	/**
	 * Constructs a new collider from the specified lower and upper bounds. This
	 * constructor assumes that all max bounds are greater than their corresponding
	 * min bounds.
	 */
	public Collider(Location min, Location max) {
		this(min.getWorld(), min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	/**
	 * Constructs a new collider from the specified bounding box.
	 */
	public Collider(World world, BoundingBox boundingBox) {
		this(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(),
				boundingBox.getMaxY(), boundingBox.getMaxZ());
	}

	/**
	 * Constructs a new axis-aligned collider. This constructor assumes that no
	 * length is negative.
	 */
	public Collider(Location center, double lengthX, double lengthY, double lengthZ) {
		this(center.getWorld(), center.getX() - lengthX / 2.0, center.getY() - lengthY / 2.0,
				center.getZ() - lengthZ / 2.0, center.getX() + lengthX / 2.0, center.getY() + lengthY / 2.0,
				center.getZ() + lengthZ / 2.0);
	}

	/**
	 * Returns whether this collider interacts with other colliders.
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * Sets whether this collider interacts with other colliders.
	 */
	public final void setActive(boolean active) {
		this.active = active;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		} else {
			// need to copy to prevent comodification in handleCollisionExit()
			List<Collider> contactsCopy = new ArrayList<>(contacts);
			for (Collider contact : contactsCopy) {
				handleCollisionExit(contact);
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
	 * Sets the world that this collider exists in.
	 */
	public final void setWorld(World world) {
		this.world = world;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Sets the minimum x, y, and z values that exist inside this collider.
	 */
	public final void setMin(double minX, double minY, double minZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum location that exists inside this collider.
	 */
	public final Location getMin() {
		return new Location(world, minX, minY, minZ);
	}

	/**
	 * Sets the minimum location that exists inside this collider.
	 */
	public final void setMin(Location min) {
		setMin(min.getX(), min.getY(), min.getZ());
	}

	/**
	 * Returns the minimum x value that exists inside this collider.
	 */
	public final double getMinX() {
		return minX;
	}

	/**
	 * Sets the minimum x value that exists inside this collider.
	 */
	public final void setMinX(double minX) {
		setMin(minX, this.minY, this.minZ);
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public final double getMinY() {
		return minY;
	}

	/**
	 * Sets the minimum y value that exists inside this collider.
	 */
	public final void setMinY(double minY) {
		setMin(this.minX, minY, this.minZ);
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public final double getMinZ() {
		return minZ;
	}

	/**
	 * Sets the minimum z value that exists inside this collider.
	 */
	public final void setMinZ(double minZ) {
		setMin(this.minX, this.minY, minZ);
	}

	/**
	 * Sets the maximum location that exists inside this collider.
	 */
	public final void setMax(double maxX, double maxY, double maxZ) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the maximum location that exists inside this collider.
	 */
	public final Location getMax() {
		return new Location(world, minX, minY, minZ);
	}

	/**
	 * Sets the maximum location that exists inside this collider.
	 */
	public final void setMax(Location max) {
		setMin(max.getX(), max.getY(), max.getZ());
	}

	/**
	 * Returns the maximum x value that exists inside this collider.
	 */
	public final double getMaxX() {
		return maxX;
	}

	/**
	 * Sets the maximum x value that exists inside this collider.
	 */
	public final void setMaxX(double maxX) {
		setMax(maxX, this.maxY, this.maxZ);
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public final double getMaxY() {
		return maxY;
	}

	/**
	 * Sets the maximum y value that exists inside this collider.
	 */
	public final void setMaxY(double maxY) {
		setMax(this.maxX, maxY, this.maxZ);
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public final double getMaxZ() {
		return maxZ;
	}

	/**
	 * Sets the maximum z value that exists inside this collider.
	 */
	public final void setMaxZ(double zMax) {
		this.maxZ = zMax;
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
		double x = (minX + maxX) / 2.0;
		double y = (minY + maxY) / 2.0;
		double z = (minZ + maxZ) / 2.0;
		Location center = new Location(world, x, y, z);
		return center;
	}

	/**
	 * Sets the center of this collider and updates all bounds accordingly.
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
		double midX = newCenter.getX();
		double halfLengthX = getLengthX() / 2.0;
		minX = midX - halfLengthX;
		maxX = midX + halfLengthX;
		double midY = newCenter.getY();
		double halfLengthY = getLengthY() / 2.0;
		minY = midY - halfLengthY;
		maxY = midY + halfLengthY;
		double midZ = newCenter.getZ();
		double halfLengthZ = getLengthZ() / 2.0;
		minZ = midZ - halfLengthZ;
		maxZ = midZ + halfLengthZ;
	}

	/**
	 * Translates this collider. Translating this collider so that it overlaps with
	 * other colliders will result in onCollisionEnter being called for each
	 * collider. Conversely, translating this collider so that it does not overlap
	 * with any colliders that it previously overlapped with will result in
	 * onCollisionExit being called for each collider.
	 */
	public final void translate(double x, double y, double z) {
		minX += x;
		maxX += x;
		minY += y;
		maxY += y;
		minZ += z;
		maxZ += z;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns the length of this collider on the x-axis.
	 */
	public final double getLengthX() {
		return maxX - minX;
	}

	/**
	 * Returns the length of this collider on the y-axis.
	 */
	public final double getLengthY() {
		return maxY - minY;
	}

	/**
	 * Returns the length of this collider on the z-axis.
	 */
	public final double getLengthZ() {
		return maxZ - minZ;
	}

	/**
	 * Sets the dimensions of the collider.
	 */
	public final void setDimensions(double lengthX, double lengthY, double lengthZ) {
		updateBounds(lengthX, lengthY, lengthZ);
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		}
	}

	/**
	 * Returns a bounding box characterized by the bounds of this collider.
	 */
	public final BoundingBox toBoundingBox() {
		return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	private final void updateBounds(double newLengthX, double newLengthY, double newLengthZ) {
		double midX = (minX + maxX) / 2.0;
		double halfLengthX = newLengthX / 2.0;
		minX = midX - halfLengthX;
		maxX = midX + halfLengthX;
		double midY = (minY + maxY) / 2.0;
		double halfLengthY = newLengthY / 2.0;
		minY = midY - halfLengthY;
		maxY = midY + halfLengthY;
		double midZ = (minZ + maxZ) / 2.0;
		double halfLengthZ = newLengthZ / 2.0;
		minZ = midZ - halfLengthZ;
		maxZ = midZ + halfLengthZ;
	}

	/**
	 * Determines what collider buckets this collider should occupy to ensure
	 * efficient and accurate collision detection. Bounds must be current to update
	 * properly.
	 */
	private final void updateOccupiedBuckets() {
		List<ColliderBucket> occupiedBucketsOld = new ArrayList<ColliderBucket>(occupiedBuckets);
		occupiedBuckets.clear();
		int bucketSize = ColliderBucket.BUCKET_SIZE;

		int bucketMinX = (int) (minX / bucketSize);
		int bucketMinY = (int) (minY / bucketSize);
		int bucketMinZ = (int) (minZ / bucketSize);

		int bucketMaxX = (int) (maxX / bucketSize);
		int bucketMaxY = (int) (maxY / bucketSize);
		int bucketMaxZ = (int) (maxZ / bucketSize);

		for (int xCount = bucketMinX; xCount <= bucketMaxX; xCount++) {
			for (int yCount = bucketMinY; yCount <= bucketMaxY; yCount++) {
				for (int zCount = bucketMinZ; zCount <= bucketMaxZ; zCount++) {
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
					ColliderBucket.removeBucket(address);
				}
			}
		}
	}

	/**
	 * Returns whether the bounds of this collider encompass a point.
	 */
	public final boolean encompasses(Location point) {
		World world = point.getWorld();
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
		return world.equals(this.world) && MathUtility.isBetween(x, minX, true, maxX, true)
				&& MathUtility.isBetween(y, minY, true, maxY, true) && MathUtility.isBetween(z, minZ, true, maxZ, true);
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
					boolean collides = isContacting(neighboringCollider);
					if (contacts.contains(neighboringCollider)) {
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
		outerloop: for (int i = 0; i < contacts.size(); i++) {
			Collider contact = contacts.get(i);
			for (int j = 0; j < occupiedBuckets.size(); j++) {
				ColliderBucket bucket = occupiedBuckets.get(j);
				if (bucket.getActiveColliders().contains(contact)) {
					continue outerloop;
				}
			}
			handleCollisionExit(contact);
		}
	}

	/**
	 * Responds to this collider and another collider entering a collision. Called
	 * when two colliders that were colliding no longer overlap each other.
	 * onCollisionEnter is called from each of the colliders.
	 */
	private final void handleCollisionEnter(Collider other) {
		this.contacts.add(other);
		other.contacts.add(this);
		this.onCollisionEnter(other);
		other.onCollisionEnter(this);
	}

	/**
	 * Responds to this collider and another collider exiting a collision. Called
	 * when two colliders that were colliding no longer overlap each other.
	 * onCollisionExit is called on each of the colliders.
	 */
	private final void handleCollisionExit(Collider other) {
		contacts.remove(other);
		other.contacts.remove(this);
		this.onCollisionExit(other);
		other.onCollisionExit(this);
	}

	/**
	 * Returns whether this collider is contacting the other collider.
	 */
	public final boolean isContacting(Collider other) {
		return (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX())
				&& (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY())
				&& (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());
	}

	/**
	 * Returns an array of colliders that this collider is currently contacting.
	 */
	public final Collider[] getContacts() {
		return contacts.toArray(new Collider[contacts.size()]);
	}

	/**
	 * Called when this collider enters a collision with another collider.
	 */
	protected void onCollisionEnter(Collider other) {
	}

	/**
	 * Called when this collider exits a collision with another collider.
	 */
	protected void onCollisionExit(Collider other) {
	}

}
