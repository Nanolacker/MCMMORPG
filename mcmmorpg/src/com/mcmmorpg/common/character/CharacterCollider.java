package com.mcmmorpg.common.character;

import org.bukkit.Location;

import com.mcmmorpg.common.physics.Collider;

/**
 * A collider that is associated with a character. Used for hitboxes.
 */
public class CharacterCollider extends Collider {

	private final AbstractCharacter character;

	public CharacterCollider(AbstractCharacter character, Location center, double lengthX, double lengthY,
			double lengthZ) {
		super(center, lengthX, lengthY, lengthZ);
		this.character = character;
	}

	public AbstractCharacter getCharacter() {
		return character;
	}

}
