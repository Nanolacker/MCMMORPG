package com.mcmmorpg.common.ai;

import org.bukkit.Location;

public class PathNode {

	private final Path path;
	private final Location location;
	private final double gCost;
	private final double hCost;

	public PathNode(Path path, Location location) {
		this.path = path;
		this.location = location;
		this.gCost = 0.0;
		this.hCost = 0.0;
	}

	public Path getPath() {
		return path;
	}

	public Location getLocation() {
		return location;
	}

	public double getFCost() {
		return gCost + hCost;
	}

	public double getGCost() {
		return gCost;
	}

	public double getHCost() {
		return hCost;
	}

	public boolean isTraversable() {
		return false;
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

}
