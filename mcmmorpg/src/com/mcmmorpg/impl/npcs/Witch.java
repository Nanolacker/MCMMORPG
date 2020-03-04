package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;

public abstract class Witch extends NonPlayerCharacter {

	private final PlayerCharacterInteractionCollider interactionBox;
	private org.bukkit.entity.Witch entity;

	protected Witch(String name, int level, Location location) {
		super(name, level, location);
		interactionBox = new PlayerCharacterInteractionCollider(location.clone().add(0, 1, 0), 1, 2, 1) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				Witch.this.onInteract(pc);
			}
		};
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (org.bukkit.entity.Witch) location.getWorld().spawnEntity(location, EntityType.WITCH);
		entity.setAI(false);
		entity.setInvulnerable(true);
		entity.setRemoveWhenFarAway(false);
		interactionBox.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
		interactionBox.setActive(false);
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return true;
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2.6, 0);
	}

	protected abstract void onInteract(PlayerCharacter pc);

}