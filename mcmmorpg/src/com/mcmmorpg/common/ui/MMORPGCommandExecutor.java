package com.mcmmorpg.common.ui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;

import net.md_5.bungee.api.ChatColor;

public class MMORPGCommandExecutor implements CommandExecutor {

	private static final String[] COMMANDS = { "help", "kill" };

	public static void registerCommands() {
		MMORPGPlugin plugin = MMORPGPlugin.getPlugin();
		MMORPGCommandExecutor executor = new MMORPGCommandExecutor();
		for (String cmdName : COMMANDS) {
			plugin.getCommand(cmdName).setExecutor(executor);
		}
	}

	private MMORPGCommandExecutor() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String cmdName = command.getName();
			if (cmdName.equalsIgnoreCase("help")) {
				player.sendMessage("too bad");
				return true;
			} else if (cmdName.equals("kill")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					pc.setAlive(false);
				}
				return true;
			}
		}
		return false;
	}

}
