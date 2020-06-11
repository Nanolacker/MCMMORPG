package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FlintonSewersUndead extends AbstractUndead {

	private static final int LEVEL = 10;
	private static final double RESPAWN_TIME = 60;

	public FlintonSewersUndead(Location spawnLocation, boolean respawn) {
		super(ChatColor.RED + "Undead", LEVEL, spawnLocation, respawn ? RESPAWN_TIME : -1, EntityType.HUSK);
	}

	@Override
	protected double maxHealth() {
		return 200;
	}

	@Override
	protected double damageAmount() {
		return 15;
	}

	@Override
	protected int xpToGrantOnDeath() {
		return 20;
	}

}
