package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class Gabe extends NonPlayerCharacter {

	protected Gabe(Location location) {
		super("G A B E", Integer.MAX_VALUE, location);
		setMaxHealth(Integer.MAX_VALUE);
	}
	
	
}
