package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class CultistSummoner extends NonPlayerCharacter {

	protected CultistSummoner(Location spawnLocation) {
		super(ChatColor.RED + "Summoner", 15, spawnLocation);
	}
	
}
