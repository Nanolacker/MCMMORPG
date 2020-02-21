package com.mcmmorpg.common.character;

import org.bukkit.Location;

import com.mcmmorpg.common.physics.Collider;

public abstract class PlayerCharacterInteractionCollider extends Collider {

	public PlayerCharacterInteractionCollider(Location center, double lengthX, double lengthY, double lengthZ) {
		super(center, lengthX, lengthY, lengthZ);
	}

	protected abstract void onInteract(PlayerCharacter pc);

}
