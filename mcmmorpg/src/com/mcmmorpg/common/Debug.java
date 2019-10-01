package com.mcmmorpg.common;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.time.DelayedTask;

public class Debug {

	private Debug() {
		// no instances
	}

	public static void log(Object message) {
		boolean initialized = MMORPGPlugin.isInitialized();
		StackTraceElement source = Thread.currentThread().getStackTrace()[2];
		if (initialized) {
			log0(message, source);
		} else {
			DelayedTask dt = new DelayedTask() {
				@Override
				public void run() {
					log0(message, source);
				}
			};
			dt.schedule();
		}
	}

	private static void log0(Object message, StackTraceElement source) {
		Server server = Bukkit.getServer();
		String formattedMessage = ChatColor.BLUE + "[DEBUG]: " + ChatColor.RESET + message + ChatColor.RESET + "\n"
				+ source.toString();
		server.broadcastMessage(formattedMessage);
	}

	/**
	 * Returns the first online player this method finds. Provides a convenient way
	 * to find a player for testing purposes.
	 */
	public static Player getFirstPlayer() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		if (players.isEmpty()) {
			return null;
		}
		return (Player) players.toArray()[0];
	}

}
