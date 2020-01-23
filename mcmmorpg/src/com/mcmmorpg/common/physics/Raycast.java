package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Deprecated
/**
 * @deprecated raycasting in some directions does not work as expected
 */
public class Raycast {

	private final Collider[] hits;

	public Raycast(Location origin, Vector direction, double maxDistance, Class<? extends Collider> target) {
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();
		// This could be made more efficient in the future.
		double bucketSize = ColliderBucket.BUCKET_SIZE;
		int currentX = (int) (origin.getX() / bucketSize);
		int currentY = (int) (origin.getY() / bucketSize);
		int currentZ = (int) (origin.getZ() / bucketSize);
		int bucketRadius = (int) Math.ceil(maxDistance / ColliderBucket.BUCKET_SIZE);
		int bucketMinX = currentX - bucketRadius;
		int bucketMaxX = currentX + bucketRadius;
		int bucketMinY = currentY - bucketRadius;
		int bucketMaxY = currentY + bucketRadius;
		int bucketMinZ = currentZ - bucketRadius;
		int bucketMaxZ = currentZ + bucketRadius;
		for (int x = bucketMinX; x < bucketMaxX; x++) {
			for (int y = bucketMinY; y < bucketMaxY; y++) {
				for (int z = bucketMinZ; z < bucketMaxZ; z++) {
					Location bucketAddress = new Location(origin.getWorld(), x, y, z);
					ColliderBucket bucket = ColliderBucket.forAddress(bucketAddress);
					if (bucket != null) {
						nearbyBuckets.add(bucket);
					}
				}
			}
		}
		List<Collider> hitsList = new ArrayList<>();
		for (ColliderBucket bucket : nearbyBuckets) {
			List<Collider> nearbyColliders = bucket.getEncompassedColliders();
			for (Collider collider : nearbyColliders) {
				BoundingBox bb = collider.toBoundingBox();
				RayTraceResult result = bb.rayTrace(origin.toVector(), direction, maxDistance);
				if (result != null) {
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
