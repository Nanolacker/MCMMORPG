package com.mcmmorpg.common.ai;

public class PathNode {

	private final double gCost;
	private final double hCost;

	public PathNode(Path path) {
		this.gCost = 0.0;
		this.hCost = 0.0;
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
					neighbors[index] = null;
					index++;
				}
			}
		}
		return neighbors;
	}

}
