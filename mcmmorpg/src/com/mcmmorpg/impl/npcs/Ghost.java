package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Ghost extends NonPlayerCharacter {

	public Drowned entity;
	
	protected Ghost(int level, Location location) {
		super(ChatColor.RED + "Ghost", level, location);
	}
	
	
	
}
