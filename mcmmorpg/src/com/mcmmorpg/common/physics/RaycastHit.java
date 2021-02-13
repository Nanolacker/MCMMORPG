package com.mcmmorpg.common.physics;

import org.bukkit.Location;

public final class RaycastHit {
    private final Collider collider;
    private final Location location;

    RaycastHit(Collider collider, Location hitLocation) {
        this.collider = collider;
        this.location = hitLocation;
    }

    public Collider getCollider() {
        return collider;
    }

    public Location getLocation() {
        return location;
    }
}
