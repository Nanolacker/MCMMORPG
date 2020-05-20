package com.mcmmorpg.common.event;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.mcmmorpg.common.MMORPGPlugin;

/**
 * Register and call events using this class.
 */
public class EventManager {

	/**
	 * Registers events from the specified listener.
	 */
	public static void registerEvents(Listener listener) {
		Plugin plugin = MMORPGPlugin.getInstance();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	/**
	 * Unregisters events of the specified type from the specified listener.
	 */
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

	/**
	 * Fires the specified event to be handled by listeners.
	 */
	public static void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}

}
