package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;

public class CharacterNavigator {

	private static final double UPDATE_PERIOD = 1.0;

	private final CharacterPathFollower pathFollower;
	private Location destination;
	private RepeatingTask updateTask;

	public CharacterNavigator(CharacterPathFollower pathFollower) {
		this.pathFollower = pathFollower;
		this.destination = null;
		updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	private void update() {
		Path path = findPath();
		pathFollower.followPath(path);
	}

	public boolean isEnabled() {
		return updateTask.isScheduled();
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			if (!updateTask.isScheduled()) {
				updateTask.schedule();
			}
		} else {
			if (updateTask.isScheduled()) {
				updateTask.cancel();
			}
		}
	}

	public Location getDestination() {
		return destination;
	}

	public void setDestination(Location destination) {
		this.destination = destination;
	}

	private Path findPath() {
		Map<Location, Costs> costs = new HashMap<>();
		List<Location> openNodes = new ArrayList<>();
		List<Location> closedNodes = new ArrayList<>();

		List<Location> waypoints = new ArrayList<>();

		Location start = pathFollower.getCharacter().getLocation();
		openNodes.add(start);

		while (!openNodes.isEmpty() && waypoints.size() < 10000) {
			// Find the current node in open set with lowest f-cost.
			int currentNodeIndex = 0;
			PathNode currentNode = openNodes.get(currentNodeIndex);
			for (int i = 1; i < openNodes.size(); i++) {
				PathNode node = openNodes.get(i);
				if (node.getFCost() < currentNode.getFCost()) {
					currentNode = node;
					currentNodeIndex = i;
				}
			}

			openNodes.remove(currentNodeIndex);
			closedNodes.add(currentNode);

			if (currentNode == targetNode) {
				pathNodes.clear();
				while (currentNode != null) {
					pathNodes.add(currentNode);
					currentNode = currentNode.getParent();
				}
				Collections.reverse(pathNodes);
				return path;
			}

			PathNode[] neighborNodes = currentNode.getNeighbors();
			for (PathNode neighborNode : neighborNodes) {
				pathNodes.add(neighborNode);
				if (!neighborNode.isTraversable() || closedNodes.contains(neighborNode)) {
					continue;
				}
				double newGCost = currentNode.getGCost() + currentNode.distance(neighborNode);
				if (newGCost < neighborNode.getGCost() || !openNodes.contains(neighborNode)) {
					openNodes.add(neighborNode);
					neighborNode.setGCost(newGCost);
					neighborNode.setHCost(neighborNode.distance(targetNode));
					neighborNode.setParent(currentNode);
					if (!openNodes.contains(neighborNode)) {
						openNodes.add(neighborNode);
					}
				}
			}
		}
		return new Path(waypoints.toArray(new Location[waypoints.size()]));
	}

	private static class Costs {
		int gCost;
		int hCost;

		int getFCost() {
			return gCost + hCost;
		}
	}

}
