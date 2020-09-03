package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Path {

	private List<PathNode> nodes;
	private PathStatus status;

	public Path() {
		this.nodes = new ArrayList<>();
	}

	public List<PathNode> getNodes() {
		return nodes;
	}

	public PathStatus getStatus() {
		return status;
	}

	public PathNode nodeForLocation(Location location) {
		for (PathNode node : nodes) {
			if (node.getLocation().equals(location)) {
				return node;
			}
		}
		return new PathNode(this, location);
	}

	public static enum PathStatus {
		PATH_COMPLETE, PATH_PARTIAL, PATH_INVALID
	}

}
