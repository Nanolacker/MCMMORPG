package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MelcherTavernKingRat extends Rat {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 50;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 15;

	public MelcherTavernKingRat(Location spawnLocation) {
		super(ChatColor.RED + "King Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

}
