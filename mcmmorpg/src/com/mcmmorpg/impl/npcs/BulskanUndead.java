package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class BulskanUndead extends AbstractUndead {

	private static final int LEVEL = 16;
	private static final double RESPAWN_TIME = 60;

	public BulskanUndead(Location spawnLocation, boolean respawn) {
		super(ChatColor.RED + "Undead", LEVEL, spawnLocation, respawn ? RESPAWN_TIME : -1, EntityType.HUSK);
	}

	@Override
	protected double maxHealth() {
		return 20;
	}

	@Override
	protected double damageAmount() {
		return 5;
	}

	@Override
	protected int xpToGrantOnDeath() {
		return 5;
	}

}
