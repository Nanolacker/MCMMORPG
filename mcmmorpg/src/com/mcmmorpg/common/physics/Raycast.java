package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.utils.Debug;

public class Raycast {

	private final Collider[] hits;

	public Raycast(Location origin, Vector direction, double maxDistance, Class<? extends Collider> target) {
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();
		// This could be made more efficient in the future.
		Location currentBucketAddress = origin.multiply(1 / ColliderBucket.BUCKET_SIZE);
		int bucketRadius = (int) Math.ceil(maxDistance / ColliderBucket.BUCKET_SIZE);
		int bucketMinX = (int) currentBucketAddress.getX() - bucketRadius;
		int bucketMaxX = (int) currentBucketAddress.getX() + bucketRadius;
		int bucketMinY = (int) currentBucketAddress.getY() - bucketRadius;
		int bucketMaxY = (int) currentBucketAddress.getY() + bucketRadius;
		int bucketMinZ = (int) currentBucketAddress.getZ() - bucketRadius;
		int bucketMaxZ = (int) currentBucketAddress.getZ() + bucketRadius;
		for (int x = bucketMinX; x < bucketMaxX; x++) {
			for (int y = bucketMinY; y < bucketMaxY; y++) {
				for (int z = bucketMinZ; z < bucketMaxZ; z++) {
					Location bucketAddress = new Location(origin.getWorld(), x, y, z);
					ColliderBucket bucket = ColliderBucket.forAddress(bucketAddress);
					nearbyBuckets.add(bucket);
				}
			}
		}

		List<Collider> hitsList = new ArrayList<>();
		for (ColliderBucket bucket : nearbyBuckets) {
			List<Collider> nearbyColliders = bucket.getEncompassedColliders();
			for (Collider collider : nearbyColliders) {
				BoundingBox bb = collider.toBoundingBox();
				RayTraceResult result = bb.rayTrace(origin.toVector(), direction, maxDistance);
				Vector hitPosition = result.getHitPosition();
				Debug.log("hit position: " + hitPosition);
				if (hitPosition != null) {
					hitsList.add(collider);
				}
			}
		}
		hits = hitsList.toArray(new Collider[hitsList.size()]);
	}

	public Raycast(Location origin, Vector direction, double maxDistance) {
		this(origin, direction, maxDistance, Collider.class);
	}

	/**
	 * Null if there was no hit.
	 */
	public Collider getFirstHit() {
		return hits[0];
	}

	public Collider[] getHits() {
		return hits;
	}

}
