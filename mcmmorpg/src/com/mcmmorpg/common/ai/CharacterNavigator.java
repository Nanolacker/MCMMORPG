package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.util.Debug;

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
		path = calculatePath();
		currentPathNodeIndex = 0;
	}

	public Path getPath() {
		return path;
	}

	public void update() {
		if (path.getNodes().isEmpty()) {
			return;
		}
		Location currentLocation = character.getLocation();
		if (currentLocation.distance(destination) < 0.2) {
			return;
		}
		Location nextNodeLocation = path.getNodes().get(currentPathNodeIndex).getLocation();
		Vector direction = nextNodeLocation.clone().subtract(currentLocation).toVector().normalize();
		Vector velocity = direction.multiply(speed);
		Location nextLocation = currentLocation.add(velocity.multiply(UPDATE_PERIOD));
		nextLocation.setDirection(direction);
		character.setLocation(nextLocation);
		if (nextLocation.distanceSquared(nextNodeLocation) < 0.1) {
			if (currentPathNodeIndex != path.getNodes().size() - 1) {
				currentPathNodeIndex++;
			}
		}
	}

	private Path calculatePath() {
		Location startLocation = character.getLocation();
		Path path = new Path();
		PathNode startNode = new PathNode(path, startLocation);
		PathNode targetNode = new PathNode(path, destination);

		path.getNodes().add(startNode);
		path.getNodes().add(targetNode);

		List<PathNode> openNodes = new ArrayList<>();
		Set<PathNode> closedNodes = new HashSet<>();
		openNodes.add(startNode);

		while (openNodes.size() > 0 && openNodes.size() < 10000) {
			PathNode currentNode = openNodes.get(0);
			double currentFCost = currentNode.getFCost();
			for (int i = 1; i < openNodes.size(); i++) {
				PathNode node = openNodes.get(i);
				double fCost = node.getFCost();
				if (fCost < currentFCost || fCost == currentFCost && node.getHCost() < currentNode.getHCost()) {
					currentNode = node;
				}
			}
			openNodes.remove(currentNode);
			closedNodes.add(currentNode);

			if (currentNode.getLocation().equals(targetNode.getLocation())) {
				path.getNodes().clear();
				while (currentNode != startNode) {
					path.getNodes().add(currentNode);
					currentNode = currentNode.getParent();
				}
				Collections.reverse(path.getNodes());
				for (PathNode node : path.getNodes()) {
					Debug.log(node.getLocation());
				}
				return path;
			}

			PathNode[] neighbors = currentNode.getNeighbors();
			for (PathNode neighborNode : neighbors) {
				boolean traversable = neighborNode.isTraversable();
				if (!traversable || closedNodes.contains(neighborNode)) {
					continue;
				}

				double gCostToNeighbor = currentNode.getGCost() + currentNode.distance(neighborNode);
				if (gCostToNeighbor < neighborNode.getGCost() || !openNodes.contains(neighborNode)) {
					neighborNode.setGCost(gCostToNeighbor);
					double hCost = neighborNode.distance(targetNode);
					neighborNode.setHCost(hCost);
					neighborNode.setParent(currentNode);

					if (!openNodes.contains(neighborNode)) {
						openNodes.add(neighborNode);
					}
				}
			}
		}
		Debug.log("didn't work");
		path.getNodes().remove(targetNode);
		return path;
	}

	// ensure that navigators don't go inside other characters

	private static class CharacterNavigationCollider extends Collider {

		public CharacterNavigationCollider(CharacterNavigator navigator) {
			super(navigator.character.getLocation().add(0, navigator.height * 0.5, 0), navigator.radius,
					navigator.radius, navigator.radius);
		}

	}

}
