package com.mcmmorpg.common.character;

import org.bukkit.Location;

import com.mcmmorpg.common.physics.Collider;

/**
 * A collider that is associated with a character. Used for hitboxes.
 */
public class CharacterCollider extends Collider {

	private final AbstractCharacter character;

	/**
	 * Creates a new character collider for the specified character.
	 */
	public CharacterCollider(AbstractCharacter character, Location center, double lengthX, double lengthY,
			double lengthZ) {
		super(center, lengthX, lengthY, lengthZ);
		this.character = character;
	}

	/**
	 * Returns the character associated with this collider.
	 */
	public AbstractCharacter getCharacter() {
		return character;
	}

}
