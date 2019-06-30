package com.mcmmorpg.common;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public void onEnable() {
		isInitialized = false;
		onMMORPGStart();
		isInitialized = true;
	}

	@Override
	public void onDisable() {
		onMMORPGStop();
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

	public void registerEvents(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	protected abstract void onMMORPGStart();

	protected abstract void onMMORPGStop();

}
