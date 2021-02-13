package com.mcmmorpg.common.ai;

import java.util.Arrays;

import org.bukkit.Location;

public class Path {
    private final Location[] waypoints;

    public Path(Location... waypoints) {
        this.waypoints = waypoints;
    }

    public Location[] getWaypoints() {
        return waypoints;
    }

    public Location getStart() {
        if (waypoints.length == 0) {
            return null;
        }
        return waypoints[0];
    }

    public Location getDestination() {
        if (waypoints.length == 0) {
            return null;
        }
        return waypoints[waypoints.length - 1];
    }

    public int getWaypointCount() {
        return waypoints.length;
    }

    public boolean isEmpty() {
        return getWaypointCount() == 0;
    }

    public Path getSubpath() {
        Location[] waypoints = Arrays.copyOfRange(this.waypoints, 1, this.waypoints.length);
        return new Path(waypoints);
    }
}
