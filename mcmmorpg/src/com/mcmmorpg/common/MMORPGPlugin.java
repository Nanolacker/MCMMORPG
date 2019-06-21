package com.mcmmorpg.common;

import org.bukkit.plugin.java.JavaPlugin;

public class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	public static boolean isInitialized() {
		return isInitialized;
	}

}
