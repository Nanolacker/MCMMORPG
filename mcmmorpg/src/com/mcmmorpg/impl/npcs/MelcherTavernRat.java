package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MelcherTavernRat extends Rat {

	private static final int LEVEL = 1;
	private static final double MAX_HEALTH = 10;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 5;

	public MelcherTavernRat(Location spawnLocation) {
		super(ChatColor.RED + "Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

}
