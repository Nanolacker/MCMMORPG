package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class ForestSpider extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final int SPEED = 1;
	private static final double MAX_HEALTH = 40;
	private static final double DAMAGE_AMOUNT = 10;

	public ForestSpider(Location spawnLocation) {
		super(ChatColor.RED + "Forest Spider", LEVEL, spawnLocation, EntityType.CAVE_SPIDER, SPEED);
		super.setHeight(1);
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
