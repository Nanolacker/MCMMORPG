package com.mcmmorpg.common.ai;

import org.bukkit.Location;

public class PathNode {

	private static final double SQRT_TWO = 1; // Math.sqrt(2.0);
	private static final double SQRT_THREE = 1; // Math.sqrt(3.0);

	private final Path path;
	private final Location location;
	private PathNode parent;
	private double gCost;
	private double hCost;

	public PathNode(Path path, Location location) {
		this.path = path;
		this.location = convertToNodeLocation(location);
		this.gCost = 0.0;
		this.hCost = 0.0;
	}

	private Location convertToNodeLocation(Location location) {
		return new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(),
				location.getBlockZ() + 0.5);
	}

	public Path getPath() {
		return path;
	}

	public Location getLocation() {
		return location;
	}

	public PathNode getParent() {
		return parent;
	}

	public void setParent(PathNode parent) {
		this.parent = parent;
	}

	public double getFCost() {
		return gCost + hCost;
	}

	public double getGCost() {
		return gCost;
	}

	public void setGCost(double gCost) {
		this.gCost = gCost;
	}

	public double getHCost() {
		return hCost;
	}

	public void setHCost(double hCost) {
		this.hCost = hCost;
	}

	public boolean isTraversable() {
		return location.clone().subtract(0, 1, 0).getBlock().getType().isSolid()
				&& location.getBlock().getType().isAir() && location.clone().add(0, 1, 0).getBlock().getType().isAir();
	}

	public PathNode[] getNeighbors() {
		PathNode[] neighbors = new PathNode[26];
		int index = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					Location neighborLocation = location.clone().add(x, y, z);
					PathNode node = path.nodeForLocation(neighborLocation);
					if (node == null) {
						node = new PathNode(path, neighborLocation);
					}
					neighbors[index] = node;
					index++;
				}
			}
		}
		return neighbors;
	}

	public double distance(PathNode other) {
		Location otherLocation = other.getLocation();
		double distanceX = Math.abs(location.getX() - otherLocation.getX());
		double distanceY = Math.abs(location.getY() - otherLocation.getY());
		double distanceZ = Math.abs(location.getZ() - otherLocation.getZ());

		double maxDistance = Math.max(Math.max(distanceX, distanceY), distanceZ);
		double midDistance = Math.max(Math.min(distanceX, distanceY),
				Math.min(Math.max(distanceX, distanceY), distanceZ));
		double minDistance = Math.min(Math.min(distanceX, distanceY), distanceZ);

		return minDistance * SQRT_THREE + (midDistance - minDistance) * SQRT_TWO + (maxDistance - minDistance);
	}

}
