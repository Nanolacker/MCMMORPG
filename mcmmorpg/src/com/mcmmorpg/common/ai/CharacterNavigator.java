package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;
import com.mcmmorpg.common.util.ParticleEffects;

public class CharacterNavigator {

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
	private Path path;
	private int currentPathNodeIndex;
	private boolean calculatingPath;

	public CharacterNavigator(Character character, double speed) {
		this.character = character;
		this.collider = new CharacterNavigationCollider(this);
		this.speed = speed;
	}

	public Location getDestination() {
		return null;
	}

	public void setDestination(Location destination) {
		this.destination = destination;
		calculatingPath = true;
		path = findPath();
		calculatingPath = false;
		currentPathNodeIndex = 0;
	}

	public Path getPath() {
		return path;
	}

	public void update() {
		if (calculatingPath) {
			return;
		}
		if (path.getNodes().isEmpty()) {
			return;
		}
		Location currentLocation = character.getLocation();
		if (currentLocation.distance(destination) < 0.2) {
			return;
		}
		Location nextTargetLocation = path.getNodes().get(currentPathNodeIndex).getLocation().clone().add(0.5, 0.0,
				0.5);
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

	private Path findPath() {
		List<PathNode> openNodes = new ArrayList<>();
		List<PathNode> closedNodes = new ArrayList<>();

		Path path = new Path();
		Location startLocation = character.getLocation();
		PathNode startNode = new PathNode(path, startLocation);
		PathNode targetNode = new PathNode(path, destination);

		List<PathNode> pathNodes = path.getNodes();
		pathNodes.add(startNode);
		pathNodes.add(targetNode);

		openNodes.add(startNode);

		while (!openNodes.isEmpty() && pathNodes.size() < 10000) {
			// Find the current node in open set with lowest f-cost.
			PathNode currentNode = openNodes.get(0);
			
			ParticleEffects.wireframeBox(Particle.CRIT, 4, currentNode.getLocation().clone().subtract(0.5, 0.0, 0.5),
					currentNode.getLocation().clone().add(0.5, 1.0, 0.5));
			
			int currentNodeIndex = 0;
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
				if (closedNodes.contains(neighborNode) || !neighborNode.isTraversable()) {
					continue;
				}
				if (!openNodes.contains(neighborNode)) {
					openNodes.add(neighborNode);
					neighborNode.setGCost(currentNode.getGCost() + currentNode.distance(neighborNode));
					neighborNode.setHCost(neighborNode.distance(targetNode));
					neighborNode.setParent(currentNode);
				} else {
					double neighborNodeGCost = neighborNode.getGCost();
					double newGCost = currentNode.getGCost() + currentNode.distance(neighborNode);
					if (newGCost < neighborNodeGCost) {
						neighborNode.setGCost(newGCost);
						neighborNode.setHCost(neighborNode.distance(targetNode));
						neighborNode.setParent(currentNode);
					}
				}
			}
		}
		pathNodes.clear();
		Debug.log("FAIL");
		return path;
	}

	private Path findPath0() {
		return null;
//		Location startLocation = character.getLocation();
//		Path path = new Path();
//		PathNode startNode = new PathNode(path, startLocation);
//		PathNode targetNode = new PathNode(path, destination);
//
//		path.getNodes().add(startNode);
//
//		List<PathNode> openNodes = new ArrayList<>();
//		Set<PathNode> closedNodes = new HashSet<>();
//		openNodes.add(startNode);
//
//		while (openNodes.size() > 0 && openNodes.size() < 10000) {
//			PathNode currentNode = openNodes.get(0);
//			double currentFCost = currentNode.getFCost();
//			for (int i = 1; i < openNodes.size(); i++) {
//				PathNode node = openNodes.get(i);
//				double fCost = node.getFCost();
//				if (fCost < currentFCost || fCost == currentFCost && node.getHCost() < currentNode.getHCost()) {
//					currentNode = node;
//				}
//			}
//			openNodes.remove(currentNode);
//			closedNodes.add(currentNode);
//
//			if (currentNode.getLocation().equals(targetNode.getLocation())) {
//				path.getNodes().clear();
//				while (currentNode != startNode) {
//					path.getNodes().add(currentNode);
//					currentNode = currentNode.getParent();
//				}
//				Collections.reverse(path.getNodes());
//				return path;
//			}
//
//			PathNode[] neighbors = currentNode.getNeighbors();
//			for (PathNode neighborNode : neighbors) {
//				boolean traversable = neighborNode.isTraversable();
//				if (!traversable || closedNodes.contains(neighborNode)) {
//					continue;
//				}
//
//				double gCostToNeighbor = currentNode.getGCost() + currentNode.distance(neighborNode);
//				if (gCostToNeighbor < neighborNode.getGCost() || !openNodes.contains(neighborNode)) {
//					neighborNode.setGCost(gCostToNeighbor);
//					double hCost = neighborNode.distance(targetNode);
//					neighborNode.setHCost(hCost);
//					neighborNode.setParent(currentNode);
//
//					if (!openNodes.contains(neighborNode)) {
//						openNodes.add(neighborNode);
//					}
//				}
//			}
//		}
//		Debug.log("didn't work");
//		path.getNodes().remove(targetNode);
//		return path;
	}

	// ensure that navigators don't go inside other characters

	private static class CharacterNavigationCollider extends Collider {

		public CharacterNavigationCollider(CharacterNavigator navigator) {
			super(navigator.character.getLocation().add(0, navigator.height * 0.5, 0), navigator.radius,
					navigator.radius, navigator.radius);
		}

	}

}
