package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class MelcherResident extends NonPlayerCharacter {

	private Location spawnLocation;
	protected Villager villager;

	public MelcherResident(String name, int level, Location spawnLocation) {
		super(name, level, spawnLocation, 10);
		this.spawnLocation = spawnLocation;
	}

	@Override
	protected void spawn() {
		super.spawn();
		villager = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
		villager.setRemoveWhenFarAway(false);
		villager.setAI(false);
		villager.setInvulnerable(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		villager.remove();
	}

}
