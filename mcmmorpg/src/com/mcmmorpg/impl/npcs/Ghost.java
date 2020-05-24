package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class Ghost extends AbstractUndead {

	private static final int LEVEL = 12;
	private static final double RESPAWN_TIME = 60;

	public Ghost(Location spawnLocation) {
		super(ChatColor.RED + "Ghost", LEVEL, spawnLocation, RESPAWN_TIME, EntityType.DROWNED);
	}

	@Override
	protected double maxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double damageAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int xpToGrantOnDeath() {
		// TODO Auto-generated method stub
		return 0;
	}

}
