package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Thief extends NonPlayerCharacter {

	protected Thief(int level, Location location) {
		super(ChatColor.RED + "Thief", level, location);
	}

}
