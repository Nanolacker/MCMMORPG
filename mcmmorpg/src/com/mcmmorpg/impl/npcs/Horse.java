package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.util.BukkitUtility;

public class Horse extends NonPlayerCharacter {

	private static final int LEVEL = 3;
	private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128);

	private org.bukkit.entity.Horse entity;

	public Horse(Location location) {
		super(ChatColor.GREEN + "Horse", LEVEL, location);
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (org.bukkit.entity.Horse) BukkitUtility.spawnNonpersistentEntity(location, EntityType.HORSE);
		entity.addPotionEffect(SLOWNESS);
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
