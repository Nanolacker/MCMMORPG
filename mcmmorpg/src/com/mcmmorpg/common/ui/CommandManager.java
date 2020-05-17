package com.mcmmorpg.common.ui;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;

public class CommandManager {

	public static void registerCommand(Command command) {
		((CraftServer) Bukkit.getServer()).getCommandMap().register("", command.bukkitCommand);
	}

}
