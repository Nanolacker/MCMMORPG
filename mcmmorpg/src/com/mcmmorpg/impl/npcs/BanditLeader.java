package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class BanditLeader extends NonPlayerCharacter {

	protected BanditLeader(String name, Location location) {
		// let user specify name
		super(name, 9, location);
	}

}
