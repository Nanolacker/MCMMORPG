package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Zombie;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;

import net.md_5.bungee.api.ChatColor;

public class Highwayman extends NonPlayerCharacter {

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private Vindicator entity;
	/**
	 * This moves the highwayman.
	 */
	private Zombie ai;
	private final double respawnTime;

	protected Highwayman(int level, Location spawnLocation, double respawnTime) {
		super(ChatColor.RED + "Highwayman", level, spawnLocation);
		super.setMaxHealth(maxHealth(level));
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 1, 0), 1, 2, 1);
		movementSyncer = new MovementSyncer(this, entity, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		this.respawnTime = respawnTime;
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}
}
