package com.mcmmorpg.common.ui;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;

/**
 * Used to register commands.
 *
 */
public class CommandManager {

	/**
	 * Registers the command, allowing it to be used.
	 */
	public static void registerCommand(Command command) {
		((CraftServer) Bukkit.getServer()).getCommandMap().register("", command.bukkitCommand);
	}

}
