package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Bandit extends NonPlayerCharacter {

	protected Bandit(int level, Location spawnLocation) {
		super(ChatColor.RED + "Bandit", level, spawnLocation);
	}

}
