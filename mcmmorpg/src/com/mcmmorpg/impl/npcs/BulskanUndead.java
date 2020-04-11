package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class BulskanUndead extends AbstractUndead {

	private static final int LEVEL = 16;
	private static final double RESPAWN_TIME = 30;

	public BulskanUndead(Location spawnLocation) {
		super(ChatColor.RED + "Undead", LEVEL, spawnLocation, RESPAWN_TIME, EntityType.HUSK);
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
