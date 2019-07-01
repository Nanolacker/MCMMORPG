package com.mcmmorpg.test;

import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Monster extends NonPlayerCharacter {

	public Monster(Location location) {
		super("Monster", 1, location);
	}

	@Override
	public void spawn() {
		
	}

}
