package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

/**
 * Stores {@link Collider}s organized based on location to increase collision
 * detection efficiency and reduce the number of unnecessary calculations.
 * {@code ColliderBucket} can be visualized as cubes lined up face to face,
 * which each cover a portion of 3-D space.
 */
final class ColliderBucket {

	/**
	 * The length that all ColliderBucket will have on all axes.
	 */
	static final int BUCKET_SIZE = 25;

	/**
	 * The Location keys are not Locations in the traditional sense. Instead they
	 * function as addresses that don't exactly represent world space like a
	 * standard Location. Their coordinate values are all integers and represent the
	 * number of buckets, whose lengths on any axis are equal to BUCKET_SIZE, that a
	 * given buckets is away from the origin on an axis.
	 */
	private static HashMap<Location, ColliderBucket> bucketMap;

	static {
		bucketMap = new HashMap<>();
	}

	/**
	 * The {@link Collider}s that are encompassed entirely or partially by this
	 * bucket.
	 */
	private List<Collider> activeColliders;
	/**
	 * Not a {@code Location} in the traditional sense.
	 */
	private Location address;

	private ColliderBucket(Location address) {
		this.address = address;
		activeColliders = new ArrayList<>();
	}

	static ColliderBucket forAddress(Location address) {
		return bucketMap.get(address);
	}

	static ColliderBucket createNewBucket(Location bucketAddress) {
		ColliderBucket bucket = new ColliderBucket(bucketAddress);
		bucketMap.put(bucketAddress, bucket);
		return bucket;
	}

	static void deleteBucket(Location address) {
		bucketMap.remove(address);
	}

	Location getAddress() {
		return address;
	}

	void encompassCollider(Collider collider) {
		activeColliders.add(collider);
	}

	void removeCollider(Collider collider) {
		activeColliders.remove(collider);
	}

	/**
	 * Contains only active colliders.
	 */
	List<Collider> getActiveColliders() {
		return activeColliders;
	}

}