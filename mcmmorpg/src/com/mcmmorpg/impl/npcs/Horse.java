package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Horse extends NonPlayerCharacter {

	private org.bukkit.entity.Horse entity;

	public Horse(String name, int level, Location location) {
		super(name, level, location);
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (org.bukkit.entity.Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
		entity.setAI(false);
		entity.setInvulnerable(true);
		entity.setAdult();
		entity.setRemoveWhenFarAway(false);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
	}

}
