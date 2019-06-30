package com.mcmmorpg.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

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
		String formattedMessage = ChatColor.BLUE + "[DEBUG]: " + ChatColor.RESET + message + "\n" + source.toString();
		server.broadcastMessage(formattedMessage);
	}

}
