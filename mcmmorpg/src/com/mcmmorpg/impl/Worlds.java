package com.mcmmorpg.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Worlds {

	public static final World ELADRADOR = Bukkit.createWorld(new WorldCreator("world_eladrador"));
	public static final World MAIN_MENU = Bukkit.createWorld(new WorldCreator("world_lobby"));

}
