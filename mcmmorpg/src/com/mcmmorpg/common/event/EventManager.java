package com.mcmmorpg.common.event;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.mcmmorpg.common.MMORPGPlugin;

public class EventManager {

	public static void registerEvents(Listener listener) {
		Plugin plugin = MMORPGPlugin.getPlugin();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	public static void unregisterEvents(Class<? extends Event> eventClass, Listener listener) {
		HandlerList handlers;
		try {
			handlers = (HandlerList) eventClass.getMethod("getHandlers").invoke(null);
			handlers.unregister(listener);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
		handlers.unregister(listener);
	}

	public static void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}

}
