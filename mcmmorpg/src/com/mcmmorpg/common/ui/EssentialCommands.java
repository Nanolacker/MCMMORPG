package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.social.Party;

/**
 * Registers commands that are essential to any implementation of MCMMORPG.
 */
public class EssentialCommands {

	public static void registerEssentialCommands() {
		Command openSkillTree = new Command("skilltree") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						pc.getPlayerClass().getSkillTree().open(pc);
					}
				}
			}
		};
		openSkillTree.setDescription("Open your skill tree.");
		Command openQuestLog = new Command("questlog") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						pc.getQuestLog().open();
					}
				}
			}
		};
		openQuestLog.setDescription("Open your quest log.");
		Command openMap = new Command("map") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						PlayerCharacterMap map = pc.getMap();
						if (map.isOpen()) {
							pc.getMap().close();
						} else {
							pc.getMap().open();
						}
					}
				}
			}
		};
		openMap.setDescription("Open/close your map.");
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
		Command partyCreate = new Command("partycreate") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						Party.create(pc);
					}
				}
			}
		};
		Command partyDisband = new Command("partydisband") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == null) {
						player.sendMessage(ChatColor.RED + "You cannot use this command right now.");
					} else {
						Party party = pc.getParty();
						if (party == null) {
							player.sendMessage(ChatColor.RED + "You are not in a party right now.");
						} else if (pc != party.getLeader()) {
							player.sendMessage(ChatColor.RED + "Only the leader can disband the party.");
						} else {
							party.disband();
						}
					}
				}
			}
		};
		Command help = new Command("help") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				sender.sendMessage(ChatColor.GRAY + "Commands");
				sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.YELLOW + "kill" + ChatColor.GRAY
						+ ": kill yourself if you get stuck");
			}
		};

		CommandManager.registerCommand(openSkillTree);
		CommandManager.registerCommand(openQuestLog);
		CommandManager.registerCommand(openMap);
		CommandManager.registerCommand(killMe);
		CommandManager.registerCommand(partyCreate);
		CommandManager.registerCommand(partyDisband);
		CommandManager.registerCommand(help);
	}

}
