package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class Raycast {

	private final Collider[] hits;

	public Raycast(Ray ray, Class<? extends Collider> target) {
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();
		double bucketSize = ColliderBucket.BUCKET_SIZE;
		Location startAsLoc = ray.getStart();
		World world = startAsLoc.getWorld();
		Vector start = startAsLoc.toVector();
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

		List<Collider> hitsList = new ArrayList<>();
		for (Collider collider : nearbyColliders) {
			if (ray.intersects(collider)) {
				hitsList.add(collider);
			}
		}
		hits = hitsList.toArray(new Collider[hitsList.size()]);
	}

	public Raycast(Ray ray) {
		this(ray, Collider.class);
	}

	/**
	 * Null if there was no hit.
	 */
	public Collider getFirstHit() {
		if (hits.length == 0) {
			return null;
		} else {
			return hits[0];
		}
	}

	public Collider[] getHits() {
		return hits;
	}

}
