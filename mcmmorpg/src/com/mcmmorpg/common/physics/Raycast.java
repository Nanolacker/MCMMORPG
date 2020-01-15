package com.mcmmorpg.common.physics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.utils.Debug;

public class Raycast {

	private final Collider hit;

	public Raycast(Location origin, Vector direction, double maxDistance, Class<? extends Collider> target) {
		List<ColliderBucket> nearbyBuckets = new ArrayList<>();
		// NEED TO FIGURE OUT HOW TO POPULATE NEARBY BUCKETS

		List<Collider> nearbyColliders = new ArrayList<>();
		for (ColliderBucket bucket : nearbyBuckets) {
			nearbyColliders.addAll(bucket.getEncompassedColliders());
		}

		for (Collider collider : nearbyColliders) {
			BoundingBox bb = collider.toBoundingBox();
			RayTraceResult result = bb.rayTrace(origin.toVector(), direction, maxDistance);
			Vector hitPosition = result.getHitPosition();
			Debug.log("hit position: " + hitPosition);
			if (hitPosition != null) {
				hit = collider;
				return;
			}
		}
		hit = null;
	}

	public Raycast(Location origin, Vector direction, double maxDistance) {
		this(origin, direction, maxDistance, Collider.class);
	}

	/**
	 * Null if there was no hit.
	 */
	public Collider getHit() {
		return hit;
	}

}
