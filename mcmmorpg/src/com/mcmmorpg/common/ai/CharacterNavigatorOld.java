package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;

public class CharacterNavigatorOld {

	private static final double UPDATE_PERIOD = 0.05;

	private final Character character;
	private Location destination;
	private final CharacterNavigationCollider collider;
	private double radius;
	private double height;
	private double speed;
	private double stepHeight;
	private double jumpLength;
	private boolean canClimbLadders;
	private PathOld path;
	private int currentPathNodeIndex;

	private RepeatingTask updateTask;

	public CharacterNavigatorOld(Character character) {
		this.character = character;
		this.destination = character.getLocation();
		this.collider = new CharacterNavigationCollider(this);
		this.speed = 0.0;
		updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
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
		path = findPath();
		currentPathNodeIndex = 0;
	}

	public PathOld getPath() {
		return path;
	}

	private void update() {
		if (path == null || path.getNodes().isEmpty()) {
			return;
		}
		Location currentLocation = character.getLocation();
		if (currentLocation.distanceSquared(destination) < 1) {
			return;
		}
		Location nextTargetLocation = path.getNodes().get(currentPathNodeIndex).getLocation();
		Vector velocity = nextTargetLocation.clone().subtract(currentLocation).toVector().normalize().multiply(speed);
		Location nextLocation = currentLocation.add(velocity.multiply(UPDATE_PERIOD));
		Vector direction = destination.clone().subtract(nextLocation).toVector().normalize();
		nextLocation.setDirection(direction);
		character.setLocation(nextLocation);
		if (nextLocation.distanceSquared(nextTargetLocation) < 0.1) {
			if (currentPathNodeIndex != path.getNodes().size() - 1) {
				currentPathNodeIndex++;
			}
		}
	}

	private PathOld findPath() {
		List<PathNode> openNodes = new ArrayList<>();
		List<PathNode> closedNodes = new ArrayList<>();

		PathOld path = new PathOld();
		Location startLocation = character.getLocation();
		PathNode startNode = new PathNode(path, startLocation);
		PathNode targetNode = new PathNode(path, destination);

		List<PathNode> pathNodes = path.getNodes();
		pathNodes.add(startNode);
		pathNodes.add(targetNode);

		openNodes.add(startNode);

		while (!openNodes.isEmpty() && pathNodes.size() < 10000) {
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
		pathNodes.clear();
		Debug.log("FAIL");
		return path;
	}

	// ensure that navigators don't go inside other characters

	private static class CharacterNavigationCollider extends Collider {

		public CharacterNavigationCollider(CharacterNavigatorOld navigator) {
			super(navigator.character.getLocation().add(0, navigator.height * 0.5, 0), navigator.radius,
					navigator.radius, navigator.radius);
		}

	}

}
