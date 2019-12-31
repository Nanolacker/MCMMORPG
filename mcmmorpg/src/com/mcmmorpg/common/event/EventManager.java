package com.mcmmorpg.common.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.mcmmorpg.common.MMORPGPlugin;

public class EventManager {

	public static void registerEvents(Listener listener) {
		Plugin plugin = MMORPGPlugin.getPlugin();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	public static void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}

}
