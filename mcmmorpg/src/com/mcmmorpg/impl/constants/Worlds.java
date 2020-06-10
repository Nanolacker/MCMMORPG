package com.mcmmorpg.impl.constants;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Contains references to all loaded worlds for convenience.
 */
public class Worlds {

	/**
	 * The world in which the game takes place.
	 */
	public static final World ELADRADOR = Bukkit.createWorld(new WorldCreator("world_eladrador"));
	/**
	 * The world in which players select their player character.
	 */
	public static final World CHARACTER_SELECTION = Bukkit.createWorld(new WorldCreator("world_character_selection"));

}
