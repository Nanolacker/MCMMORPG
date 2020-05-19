package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Chicken extends NonPlayerCharacter {

	private final Location spawnLocation;
	private final MovementSynchronizer movementSyncer;
	private org.bukkit.entity.Chicken entity;

	public Chicken(Location spawnLocation) {
		super(ChatColor.GREEN + "Chicken", 1, spawnLocation);
		this.spawnLocation = spawnLocation;
		this.movementSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		super.setHeight(1);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		entity = (org.bukkit.entity.Chicken) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.CHICKEN);
		entity.setInvulnerable(true);
		entity.setAdult();
		entity.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		movementSyncer.setEnabled(false);
		entity.remove();
	}

}
