package com.mcmmorpg.common.persistence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A representation of a location that can be serialized.
 */
public final class PersistentLocation {
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public PersistentLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Creates a persistent location from the specified location.
     */
    public PersistentLocation(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Converts this persistent location to a location.
     */
    public Location toLocation() {
        World world = getWorld();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
