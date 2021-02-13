package com.mcmmorpg.common.event;

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
    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /**
     * Fires the specified event to be handled by listeners.
     */
    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
