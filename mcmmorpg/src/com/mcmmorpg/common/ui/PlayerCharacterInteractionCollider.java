package com.mcmmorpg.common.ui;

import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.physics.Collider;

/**
 * A collider for areas with which player characters can interact by
 * right-clicking.
 */
public abstract class PlayerCharacterInteractionCollider extends Collider {

	/**
	 * Creates a new interaction collider.
	 */
	public PlayerCharacterInteractionCollider(Location center, double lengthX, double lengthY, double lengthZ) {
		super(center, lengthX, lengthY, lengthZ);
	}

	/**
	 * Called when a player character interacts with this collider.
	 */
	public abstract void onInteract(PlayerCharacter pc);

}
