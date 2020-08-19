package com.mcmmorpg.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.mcmmorpg.common.item.ItemRarity;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;

public class Constants {

	public static World TEST_WORLD;
	public static final PlayerClass TEST_PLAYER_CLASS;
	public static final Weapon TEST_WEAPON;
	public static final Location TEST_SPAWN_LOCATION;

	static {
		TEST_WORLD = Bukkit.createWorld(new WorldCreator("world_eladrador"));
		TEST_SPAWN_LOCATION = new Location(TEST_WORLD, 0, 69, 0);
		Skill[] skills = {};
		TEST_PLAYER_CLASS = new PlayerClass("Test Class", skills);
		TEST_PLAYER_CLASS.initialize();
		TEST_WEAPON = new Weapon("Test", ItemRarity.LEGENDARY, Material.IRON_SWORD, null, TEST_PLAYER_CLASS.getName(),
				1, 10);
		TEST_WEAPON.initialize();
	}

}
