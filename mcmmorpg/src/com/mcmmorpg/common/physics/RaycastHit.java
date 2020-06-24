package com.mcmmorpg.common.physics;

import org.bukkit.Location;

public class RaycastHit {

	private final Collider collider;
	private final Location hitLocation;

	RaycastHit(Collider collider, Location hitLocation) {
		this.collider = collider;
		this.hitLocation = hitLocation;
	}

	public Collider getCollider() {
		return collider;
	}

	public Location getHitLocation() {
		return hitLocation;
	}

}
