package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class FlintonSewersRat extends Rat {

	private static final int LEVEL = 10;
	private static final double MAX_HEALTH = 75;
	private static final double DAMAGE_AMOUNT = 10;
	private static final int XP_REWARD = 20;

	public FlintonSewersRat(Location spawnLocation) {
		super(ChatColor.RED + "Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

}
