package com.mcmmorpg.common.character;

import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;

public final class Xp {

	/**
	 * Give player characters XP in an area.
	 */
	public static void distributeXp(Location location, double radius, int amount) {
		double diameter = radius * 2;
		Collider bounds = new Collider(location, diameter, diameter, diameter) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.giveXp(amount);
				}
			}
		};
		bounds.setActive(true);
		bounds.setActive(false);
	}
}
