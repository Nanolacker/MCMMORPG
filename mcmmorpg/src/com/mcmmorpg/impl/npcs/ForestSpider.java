package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class ForestSpider extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final double MAX_HEALTH = 40;
	private static final double DAMAGE_AMOUNT = 10;

	public ForestSpider(Location spawnLocation) {
		super(ChatColor.RED + "Forest Spider", LEVEL, spawnLocation);
	}

	@Override
	protected double maxHealth() {
		return MAX_HEALTH;
	}

	@Override
	protected double damageAmount() {
		return DAMAGE_AMOUNT;
	}

}
