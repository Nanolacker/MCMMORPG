package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.MathUtils;

/**
 * Represents an axis-aligned box collider.
 */
public abstract class Collider {

	/**
	 * The particle used to draw collideres.
	 */
	private static final Particle DEFAULT_DRAW_PARTICLE = Particle.CRIT;
	/**
	 * The period by which collider will be drawn.
	 */
	private static final double DRAW_PERIOD = 0.1;
	/**
	 * How thick layers of particles used to draw collideres will be. The greater
	 * this value, the less space there is between particles used to draw
	 * collideres.
	 */
	private static final double DRAW_THICKNESS = 4.0;

	/**
	 * Whether this collider will collide and respond to other collideres.
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
	 * Whether this collider should be "drawn" in its Minecraft world using
	 * particles to visualize its location and size.
	 */
	private boolean drawingEnabled;
	/**
	 * The type of pattern in which this collider will be drawn when
	 * {@code drawingEnabled} is true. The default mode is
	 * {@code ColliderDrawMode.WIREFRAME}.
	 */
	private ColliderDrawMode drawMode;
	/**
	 * The particle that will be used to draw this collider if
	 * {@code drawingEnabled} is true.
	 */
	private Particle drawParticle;
	/**
	 * The repeating task that is used to draw this collider.
	 */
	private RepeatingTask drawTask;
	/**
	 * The collider buckets that this collider occupies.
	 */
	private List<ColliderBucket> occupiedBuckets;
	/**
	 * The collideres that this collider is currently colliding with.
	 */
	private List<Collider> collidingColliders;

	/**
	 * Represents a type of pattern by which collideres can be drawn.
	 */
	public static enum ColliderDrawMode {
		/**
		 * Results in a wireframe visual representation of a collider when it is drawn
		 * using this mode.
		 */
		WIREFRAME,
		/**
		 * The entirety of collider will be filled when it is drawn using this mode.
		 */
		FILL
	}

	/**
	 * Constructs a new axis-aligned, cuboid collider. The max value on any axis
	 * must be greater than that axis's min value. Be sure to invoke
	 * {@code setActive} to activate this collider after construction.
	 * 
	 * @param world the world this collider will exist in
	 * @param xMin  the minimum x value that exists within this collider
	 * @param xMax  the maximum x value that exists within this collider
	 * @param yMin  the minimum y value that exists within this collider
	 * @param yMax  the maximum y value that exists within this collider
	 * @param zMin  the minimum z value that exists within this collider
	 * @param zMax  the maximum z value that exists within this collider
	 */
	public Collider(World world, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		this.world = world;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		init();
	}

	/**
	 * Constructs a new {@code Collider} from the specified {@link BoundingBox}.
	 * 
	 * @param world       the world this collider will exist in
	 * @param boundingBox the collider used to construct this {@code Collider}
	 */
	public Collider(World world, BoundingBox boundingBox) {
		this(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(),
				boundingBox.getMaxY(), boundingBox.getMaxZ());
	}

	/**
	 * Constructs a new axis-aligned collider. Any length of this collider should not
	 * be negative. Be sure to invoke {@code setActive} to activate collider after
	 * construction.
	 * 
	 * @param center  the location, including the world, at the center of this
	 *                collider
	 * @param lengthX the length of this collider on the x-axis
	 * @param lengthY the length of this collider on the y-axis
	 * @param lengthZ the length of this collider on the z-axis
	 * 
	 * @throws IllegalArgumentException if any of the lengths are negative
	 */
	public Collider(Location center, double lengthX, double lengthY, double lengthZ) {
		this(center.getWorld(), center.getX() - lengthX / 2, center.getX() + lengthX / 2, center.getY() - lengthY / 2,
				center.getY() + lengthY / 2, center.getZ() - lengthZ / 2, center.getZ() + lengthZ / 2);
	}

	/**
	 * Eliminates redundancy in constructors.
	 */
	private void init() {
		active = false;
		drawingEnabled = false;
		drawMode = ColliderDrawMode.WIREFRAME;
		drawParticle = DEFAULT_DRAW_PARTICLE;
		drawTask = null;
		occupiedBuckets = new ArrayList<ColliderBucket>();
		collidingColliders = new ArrayList<Collider>();
	}

	/**
	 * Returns whether this collider will interact with other collideres.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets whether this collider will interact with other collideres.
	 */
	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			updateOccupiedBuckets();
			checkForCollision();
		} else {
			for (int i = 0; i < collidingColliders.size(); i++) {
				Collider collidingWith = collidingColliders.get(i);
				onCollisionExit(collidingWith);
				collidingWith.onCollisionExit(this);
			}
		}
	}

	/**
	 * Returns the world this collider exists in.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets the world that this collider will exist in.
	 * 
	 * @param world the world this collider will exist in
	 */
	public void setWorld(World world) {
		this.world = world;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum x value that exists inside this collider.
	 */
	public double getXMin() {
		return xMin;
	}

	public void setXMin(double xMin) {
		this.xMin = xMin;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public double getYMin() {
		return yMin;
	}

	public void setYMin(double yMin) {
		this.yMin = yMin;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public double getZMin() {
		return zMin;
	}

	public void setZMin(double zMin) {
		this.zMin = zMin;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the maximum x value that exists inside this collider.
	 */
	public double getXMax() {
		return xMax;
	}

	public void setXMax(double xMax) {
		this.xMax = xMax;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum y value that exists inside this collider.
	 */
	public double getYMax() {
		return yMax;
	}

	public void setYMax(double yMax) {
		this.yMax = yMax;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the minimum z value that exists inside this collider.
	 */
	public double getZMax() {
		return zMax;
	}

	public void setZMax(double zMax) {
		this.zMax = zMax;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the location of the point that exists at the center of this bounding
	 * box.
	 */
	public Location getCenter() {
		double x = (xMin + xMax) / 2;
		double y = (yMin + yMax) / 2;
		double z = (zMin + zMax) / 2;
		Location center = new Location(world, x, y, z);
		return center;
	}

	/**
	 * Sets the center of this collider and updates all bounds accordingly.
	 * 
	 * @param center the new center of this collider
	 */
	public void setCenter(Location center) {
		world = center.getWorld();
		updateBounds(center);
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns the length of this collider on the x-axis.
	 */
	public double getLengthX() {
		return xMax - xMin;
	}

	/**
	 * Returns the length of this collider on the y-axis.
	 */
	public double getLengthY() {
		return yMax - yMin;
	}

	/**
	 * Returns the length of this collider on the z-axis.
	 */
	public double getLengthZ() {
		return zMax - zMin;
	}

	/**
	 * Returns the dimensions of this collider.
	 */
	public Vector getDimensions() {
		double lengthX = getLengthX();
		double lengthY = getLengthY();
		double lengthZ = getLengthZ();
		return new Vector(lengthX, lengthY, lengthZ);
	}

	/**
	 * Sets the dimensions of the collider.
	 * 
	 * @param dimensions the new dimensions of this collider
	 */
	public void setDimensions(Vector dimensions) {
		updateBounds(dimensions);
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Translates this collider. Translating this collider so that it overlaps with
	 * another collideres will result in {@link Collider#onCollisionEnter} being
	 * called for each collider. Conversely, translating this collider so that it
	 * does not overlap with any collideres that it previously overlapped with will
	 * result in {@link Collider#onCollisionExit} being called for each collider.
	 * 
	 * @param translate vector by which to translate this collider
	 */
	public void translate(Vector translate) {
		double x = translate.getX();
		double y = translate.getY();
		double z = translate.getZ();
		xMin += x;
		xMax += x;
		yMin += y;
		yMax += y;
		zMin += z;
		zMax += z;
		updateOccupiedBuckets();
		if (active) {
			checkForCollision();
		}
	}

	/**
	 * Returns whether this collider encompasses a point (in other words, whether
	 * this point exists within the volume of this collider).
	 * 
	 * @param point location of the point to be checked
	 * @return whether this collider encompasses the point
	 */
	public boolean encompasses(Location point) {
		World world = point.getWorld();
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
		return world.equals(this.world) && MathUtils.checkInInterval(x, xMin, true, xMax, true)
				&& MathUtils.checkInInterval(y, yMin, true, yMax, true)
				&& MathUtils.checkInInterval(z, zMin, true, zMax, true);
	}

	/**
	 * Enabling drawing will result in a visual representation of this collider to
	 * be rendered in game using particles. As creating so many particles is very
	 * costly, this should only be invoked for debugging purposes.
	 */
	public void setDrawingEnabled(boolean enabled) {
		boolean redundant = this.drawingEnabled == enabled;
		if (redundant) {
			return;
		}
		this.drawingEnabled = enabled;
		if (drawingEnabled) {
			if (drawTask == null) {
				assignDrawTask();
			}
			drawTask.schedule();
			Location center = getCenter();
			String centerDesc = String.format(ChatColor.YELLOW + "center = (%.1f, %.1f, %.1f)", center.getX(),
					center.getY(), center.getZ());
			Debug.log(ChatColor.WHITE + "Drawing of collider has been enabled. (" + centerDesc + ChatColor.WHITE + ")");
		} else {
			drawTask.cancel();
		}
	}

	/**
	 * Changes the mode, or pattern, used to draw this collider.
	 * 
	 * @param mode the mode with which this collider will be drawn
	 */
	public void setDrawMode(ColliderDrawMode mode) {
		drawMode = mode;
		if (drawingEnabled) {
			drawTask.cancel();
		}
		assignDrawTask();
		if (drawingEnabled) {
			drawTask.schedule();
		}
	}

	/**
	 * Changes the particle used to draw this collider when drawing is enabled.
	 * 
	 * @param particle the particle to be used in drawing this collider
	 */
	public void setDrawParticle(Particle particle) {
		drawParticle = particle;
	}

	/**
	 * Assigns {@code drawTask}, the task used to draw this collider.
	 */
	private void assignDrawTask() {
		drawTask = new RepeatingTask(DRAW_PERIOD) {

			@Override
			protected void run() {
				switch (drawMode) {
				case WIREFRAME:
					drawWireframe();
					break;
				case FILL:
					drawFill();
					break;
				default:
					break;
				}
			}

		};
	}

	/**
	 * Draws this collider in a wireframe pattern.
	 */
	private void drawWireframe() {
		// distance between particles used to draw
		double spaceDistance = 1 / DRAW_THICKNESS;
		World world = getWorld();
		// represents whether xCount has reached reached its maximum
		boolean xFinished = false;
		for (double xCount = xMin; xCount <= xMax && !xFinished; xCount += spaceDistance) {
			// represents whether yCount has reached reached its maximum
			boolean yFinished = false;
			for (double yCount = yMin; yCount <= yMax && !yFinished; yCount += spaceDistance) {
				// represents whether zCount has reached reached its maximum
				boolean zFinished = false;
				for (double zCount = zMin; zCount <= zMax && !zFinished; zCount += spaceDistance) {
					int validCount = 0;
					if (xCount == xMin) {
						validCount++;
					}
					if (xCount > xMax - spaceDistance) {
						validCount++;
						xFinished = true;
					}
					if (yCount == yMin) {
						validCount++;
					}
					if (yCount > yMax - spaceDistance) {
						validCount++;
						yFinished = true;
					}
					if (zCount == zMin) {
						validCount++;
					}
					if (zCount > zMax - spaceDistance) {
						validCount++;
						zFinished = true;
					}
					boolean validPoint = validCount >= 2;
					if (validPoint) {
						Location point = new Location(world, xCount, yCount, zCount);
						world.spawnParticle(drawParticle, point, 0);
					}
				}
			}
		}
	}

	/**
	 * Draws this collider in a fill pattern (i.e. the drawing is completely filled
	 * with particles).
	 */
	private void drawFill() {
		// distance between particles used to draw
		double spaceDistance = 1 / DRAW_THICKNESS;
		for (double xCount = xMin; xCount <= xMax; xCount += spaceDistance) {
			for (double yCount = yMin; yCount <= yMax; yCount += spaceDistance) {
				for (double zCount = zMin; zCount <= zMax; zCount += spaceDistance) {
					Location point = new Location(world, xCount, yCount, zCount);
					world.spawnParticle(drawParticle, point, 0);
				}
			}
		}
	}

	private void updateBounds(Location center) {
		double xMid = center.getX();
		double halfLengthX = getLengthX() / 2;
		xMin = xMid - halfLengthX;
		xMax = xMid + halfLengthX;
		double yMid = center.getY();
		double halfLengthY = getLengthY() / 2;
		yMin = yMid - halfLengthY;
		yMax = yMid + halfLengthY;
		double zMid = center.getZ();
		double halfLengthZ = getLengthZ() / 2;
		zMin = zMid - halfLengthZ;
		zMax = zMid + halfLengthZ;
	}

	private void updateBounds(Vector dimensions) {
		double xMid = (xMin + xMax) / 2;
		double halfLengthX = dimensions.getX() / 2;
		xMin = xMid - halfLengthX;
		xMax = xMid + halfLengthX;
		double yMid = (yMin + yMax) / 2;
		double halfLengthY = dimensions.getY() / 2;
		yMin = yMid - halfLengthY;
		yMax = yMid + halfLengthY;
		double zMid = (zMin + zMax) / 2;
		double halfLengthZ = dimensions.getZ() / 2;
		zMin = zMid - halfLengthZ;
		zMax = zMid + halfLengthZ;
	}

	/**
	 * Determines what collider buckets this collider should exist in to ensure
	 * efficient and accurate collision detection. Bounds must be current to update
	 * properly.
	 */
	private void updateOccupiedBuckets() {
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
					ColliderBucket bucket = ColliderBucket.bucketByAddress(bucketAddress);
					if (bucket == null) {
						bucket = ColliderBucket.createNewBucket(bucketAddress);
					}
					boolean alreadyEncompassed = bucket.getEncompassedColliders().contains(this);
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
				if (bucket.getEncompassedColliders().isEmpty()) {
					Location address = bucket.getAddress();
					ColliderBucket.deleteBucket(address);
				}
			}
		}
	}

	/**
	 * Detects the presence and absence of collisions between this collider and
	 * other colliders and responds appropriately.
	 */
	private void checkForCollision() {
		for (int i = 0; i < occupiedBuckets.size(); i++) {
			ColliderBucket bucket = occupiedBuckets.get(i);
			List<Collider> neighboringColliders = bucket.getEncompassedColliders();
			for (int j = 0; j < neighboringColliders.size(); j++) {
				Collider neighboringCollider = neighboringColliders.get(j);
				if (neighboringCollider != this && neighboringCollider.isActive() == true) {
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
				if (bucket.getEncompassedColliders().contains(collidingCollider)) {
					continue outerloop;
				}
			}
			handleCollisionExit(collidingCollider);
		}
	}

	/**
	 * Responds to this collider and another collider colliding with each other.
	 * Called when two collideres that were colliding no longer overlap each other.
	 * {@code onCollisionEnter} is called from each of the bounding boxes.
	 * 
	 * @param other the other collider in the collision
	 */
	private void handleCollisionEnter(Collider other) {
		this.collidingColliders.add(other);
		other.collidingColliders.add(this);
		this.onCollisionEnter(other);
		other.onCollisionEnter(this);
	}

	/**
	 * Responds to this collider and another collider retracting from a collision.
	 * Called when two collideres that were colliding no longer overlap each other.
	 * {@code onCollisionExit} is called from each of the collideres.
	 * 
	 * @param other the other collider in the collision
	 */
	private void handleCollisionExit(Collider other) {
		collidingColliders.remove(other);
		other.collidingColliders.remove(this);
		this.onCollisionExit(other);
		other.onCollisionExit(this);
	}

	/**
	 * Returns whether this {@code Collider} is colliding with the specified
	 * {@code Collider}.
	 * 
	 * @param other the other {@code Collider}
	 * @return whether the two {@code Collider}s are colliding
	 */
	public boolean isCollidingWith(Collider other) {
		return (this.getXMin() <= other.getXMax() && this.getXMax() >= other.getXMin())
				&& (this.getYMin() <= other.getYMax() && this.getYMax() >= other.getYMin())
				&& (this.getZMin() <= other.getZMax() && this.getZMax() >= other.getZMin());
	}

	/**
	 * Called when this collider enters a collision with another collider.
	 * 
	 * @param other the other collider in the collision
	 */
	protected abstract void onCollisionEnter(Collider other);

	/**
	 * Called when this collider exits a collision with another collider.
	 * 
	 * @param other the other collider in the collision
	 */
	protected abstract void onCollisionExit(Collider other);

}