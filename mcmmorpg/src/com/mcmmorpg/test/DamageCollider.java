package com.mcmmorpg.test;

import org.bukkit.Location;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.CommonCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.physics.Collider;

public class DamageCollider extends Collider {

	public DamageCollider(Location center) {
		super(center, 5, 5, 5);
	}

	@Override
	protected void onCollisionEnter(Collider other) {
		if (other instanceof CharacterCollider) {
			CommonCharacter character = ((CharacterCollider) other).getCharacter();
			if (character instanceof PlayerCharacter) {
				return;
			}
			character.setCurrentHealth(character.getCurrentHealth() - 5);
		}
	}

	@Override
	protected void onCollisionExit(Collider other) {
	}

}
