package com.mcmmorpg.common.ai;

import org.bukkit.Location;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.physics.Collider;

public class CharacterNavigator {

	private final Character character;
	private final CharacterNavigationCollider collider;
	private double radius;
	private double height;
	private double baseOffset;
	private double speed;
	private double stepHeight;

	public CharacterNavigator(Character character) {
		this.character = character;
		this.collider = new CharacterNavigationCollider(this);
	}

	public Location getDestination() {
		return null;
	}

	public void setDestination(Location destination) {

	}

	// ensure that navigators don't go inside other characters

	private static class CharacterNavigationCollider extends Collider {

		public CharacterNavigationCollider(CharacterNavigator navigator) {
			super(navigator.character.getLocation().add(0, navigator.baseOffset, 0), navigator.radius, navigator.radius,
					navigator.radius);
		}

	}

}
