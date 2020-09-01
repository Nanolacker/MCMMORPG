package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.physics.Collider;

public class CharacterNavigator {

	private static final double UPDATE_PERIOD = 0.05;

	private final Character character;
	private final CharacterNavigationCollider collider;
	private double radius;
	private double height;
	private double speed;
	private double stepHeight;
	private double jumpLength;

	public CharacterNavigator(Character character) {
		this.character = character;
		this.collider = new CharacterNavigationCollider(this);
	}

	public Location getDestination() {
		return null;
	}

	public void setDestination(Location destination) {

	}

	private List<Location> getPath() {
		List<Location> openNodes = new ArrayList<>();
		List<Location> closedNodes = new ArrayList<>();

		Location startNode = character.getLocation();
		openNodes.add(startNode);

		for (;;) {
			Location currentNode = null;// node in open with lowest f cost
			openNodes.remove(currentNode);
			closedNodes.add(currentNode);

			if (currentNodeIsTarget) {
				return openNodes;
			}

			Location[] neighborNodes = {};
			for (Location neighborNode : neighborNodes) {
				boolean traversable;
				if (!traversable || closedNodes.contains(neighborNode)) {
					continue;
				}

				if (openNodes.contains(neighborNode)) {

				}

			}
		}
	}

	// ensure that navigators don't go inside other characters

	private static class CharacterNavigationCollider extends Collider {

		public CharacterNavigationCollider(CharacterNavigator navigator) {
			super(navigator.character.getLocation().add(0, navigator.height * 0.5, 0), navigator.radius,
					navigator.radius, navigator.radius);
		}

	}

}
