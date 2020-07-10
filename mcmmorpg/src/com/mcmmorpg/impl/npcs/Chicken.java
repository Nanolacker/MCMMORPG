package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.common.ai.MotionSynchronizer;
import com.mcmmorpg.common.ai.MotionSynchronizer.MotionSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Chicken extends NonPlayerCharacter {

	private final Location spawnLocation;
	private final MotionSynchronizer motionSyncer;
	private org.bukkit.entity.Chicken entity;

	public Chicken(Location spawnLocation) {
		super(ChatColor.GREEN + "Chicken", 1, spawnLocation);
		this.spawnLocation = spawnLocation;
		this.motionSyncer = new MotionSynchronizer(this, MotionSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		super.setHeight(1);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		entity = (org.bukkit.entity.Chicken) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.CHICKEN);
		entity.setInvulnerable(true);
		entity.setAdult();
		entity.setRemoveWhenFarAway(false);
		motionSyncer.setEntity(entity);
		motionSyncer.setEnabled(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		motionSyncer.setEnabled(false);
		entity.remove();
	}

}
