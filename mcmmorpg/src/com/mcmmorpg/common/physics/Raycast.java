package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Casts a ray to check for collisions.
 */
public final class Raycast {

	private final List<RaycastHit> hits;

	/**
	 * Creates a raycast with the specified ray. The raycast will only target
	 * colliders of the specified class.
	 */
	public Raycast(Ray ray, Class<? extends Collider> target) {
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();
		double bucketSize = ColliderBucket.BUCKET_SIZE;
		Location startLocation = ray.getStart();
		World world = startLocation.getWorld();
		Vector start = startLocation.toVector();
		Vector end = ray.getEnd().toVector();
		Vector min = Vector.getMinimum(start, end);
		Vector max = Vector.getMaximum(start, end);

		int bucketMinX = (int) (min.getX() / bucketSize);
		int bucketMinY = (int) (min.getY() / bucketSize);
		int bucketMinZ = (int) (min.getZ() / bucketSize);

		int bucketMaxX = (int) (max.getX() / bucketSize);
		int bucketMaxY = (int) (max.getY() / bucketSize);
		int bucketMaxZ = (int) (max.getZ() / bucketSize);

		for (int x = bucketMinX; x <= bucketMaxX; x++) {
			for (int y = bucketMinY; y <= bucketMaxY; y++) {
				for (int z = bucketMinZ; z <= bucketMaxZ; z++) {
					Location bucketAddress = new Location(world, x, y, z);
					ColliderBucket bucket = ColliderBucket.forAddress(bucketAddress);
					if (bucket != null) {
						nearbyBuckets.add(bucket);
					}
				}
			}
		}

		List<Collider> nearbyColliders = new ArrayList<>();
		for (ColliderBucket bucket : nearbyBuckets) {
			List<Collider> encompassedColliders = bucket.getActiveColliders();
			for (Collider collider : encompassedColliders) {
				if (target.isAssignableFrom(collider.getClass()) && !nearbyColliders.contains(collider)) {
					nearbyColliders.add(collider);
				}
			}
		}

		hits = new ArrayList<>();
		for (Collider collider : nearbyColliders) {
			Location hitLocation = ray.getIntsersection(collider);
			if (hitLocation != null) {
				RaycastHit hit = new RaycastHit(collider, hitLocation);
				hits.add(hit);
			}
		}

		RaycastHitComparator comparator = new RaycastHitComparator(startLocation);
		Collections.sort(hits, comparator);
	}

	/**
	 * Creates a raycast with the specified ray. The raycast will target colliders
	 * of all classes.
	 */
	public Raycast(Ray ray) {
		this(ray, Collider.class);
	}

	public List<RaycastHit> getHits() {
		return hits;
	}

	private static final class RaycastHitComparator implements Comparator<RaycastHit> {

		private Location rayStart;

		private RaycastHitComparator(Location rayStart) {
			this.rayStart = rayStart;
		}

		@Override
		public int compare(RaycastHit hit1, RaycastHit hit2) {
			Location hitLocation1 = hit1.getHitLocation();
			Location hitLocation2 = hit2.getHitLocation();

			double distanceSquared1 = hitLocation1.distanceSquared(rayStart);
			double distanceSquared2 = hitLocation2.distanceSquared(rayStart);

			return (int) (distanceSquared1 - distanceSquared2);
		}

	}

}
