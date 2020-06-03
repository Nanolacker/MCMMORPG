package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * Registers commands that are essential to any implementation of MCMMORPG.
 */
public class EssentialCommands {

	public static void registerEssentialCommands() {
		Command help = new Command("help") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				sender.sendMessage(ChatColor.GRAY + "Help feature coming soon.");
			}
		};
		Command killMe = new Command("kill") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						pc.sendMessage(ChatColor.GRAY + "You killed yourself");
						pc.setAlive(false);
					}
				}
			}
		};
		killMe.setDescription("If your character is stuck, use this to respawn elsewhere.");

		CommandManager.registerCommand(help);
		CommandManager.registerCommand(killMe);
	}

}
