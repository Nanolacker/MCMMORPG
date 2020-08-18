package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Casts a ray to check for collisions.
 */
public final class Raycast {

	private final List<RaycastHit> hits;

	/**
	 * The raycast will only target colliders of the specified class.
	 */
	public Raycast(Location start, Vector direction, double maxDistance, Class<? extends Collider> target) {
		World world = start.getWorld();
		direction = direction.clone();
		if (!direction.isNormalized()) {
			direction.normalize();
		}
		Vector startAsVector = start.toVector();
		Vector endAsVector = startAsVector.clone().add(direction.multiply(maxDistance));
		Vector min = Vector.getMinimum(startAsVector, endAsVector);
		Vector max = Vector.getMaximum(startAsVector, endAsVector);
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();

		int bucketMinX = (int) (min.getX() / ColliderBucket.BUCKET_SIZE);
		int bucketMinY = (int) (min.getY() / ColliderBucket.BUCKET_SIZE);
		int bucketMinZ = (int) (min.getZ() / ColliderBucket.BUCKET_SIZE);

		int bucketMaxX = (int) (max.getX() / ColliderBucket.BUCKET_SIZE);
		int bucketMaxY = (int) (max.getY() / ColliderBucket.BUCKET_SIZE);
		int bucketMaxZ = (int) (max.getZ() / ColliderBucket.BUCKET_SIZE);

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
			Location intersection = getRayIntersection(start, direction, maxDistance, collider);
			if (intersection != null) {
				RaycastHit hit = new RaycastHit(collider, intersection);
				hits.add(hit);
			}
		}

		RaycastHitComparator comparator = new RaycastHitComparator(start);
		Collections.sort(hits, comparator);
	}

	public Raycast(Location start, Location end, Class<? extends Collider> target) {
		this(start, end.toVector().subtract(start.toVector()).normalize(), start.distance(end), target);
	}

	private Location getRayIntersection(Location start, Vector direction, double maxDistance, Collider collider) {
		RayTraceResult result = collider.toBoundingBox().rayTrace(start.toVector(), direction, maxDistance);
		if (result == null) {
			return null;
		} else {
			return result.getHitPosition().toLocation(start.getWorld());
		}
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
