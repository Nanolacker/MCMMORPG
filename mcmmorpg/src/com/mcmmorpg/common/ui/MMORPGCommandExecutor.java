package com.mcmmorpg.common.ui;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.utils.Debug;

import net.md_5.bungee.api.ChatColor;

public class MMORPGCommandExecutor implements CommandExecutor {

	private static final String[] COMMANDS = { "help", "kill", "printlocation", "heal", "restoremana", "grantxp",
			"giveitem", "startquest", "completequest" };

	public static void registerCommands() {
		MMORPGPlugin plugin = MMORPGPlugin.getInstance();
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
				player.sendMessage("Feature not ready yet");
				return true;
			} else if (cmdName.equalsIgnoreCase("kill")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					pc.sendMessage(ChatColor.GRAY + "You killed yourself");
					pc.setAlive(false);
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("printlocation")) {
				Location location = player.getLocation();
				int x = (int) location.getX();
				int y = (int) location.getY();
				int z = (int) location.getZ();
				int yaw = (int) location.getYaw();
				int pitch = (int) location.getPitch();
				Debug.log(x + ", " + y + ", " + z + ", " + yaw + ", " + pitch);
				return true;
			} else if (cmdName.equalsIgnoreCase("heal")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					pc.heal(Double.parseDouble(args[0]), null);
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("restoremana")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					pc.setCurrentMana(pc.getCurrentMana() + Double.parseDouble(args[0]));
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("grantxp")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					pc.grantXp(Integer.parseInt(args[0]));
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("giveitem")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					Item item = Item.forName(args[0]);
					pc.giveItem(item);
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("startquest")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					String questName = args[0];
					questName = questName.replace("_", " ");
					Quest quest = Quest.forName(questName);
					quest.start(pc);
				}
				return true;
			} else if (cmdName.equalsIgnoreCase("completequest")) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					player.sendMessage(ChatColor.RED + "Cannot use this command right now");
				} else {
					String questName = args[0];
					questName = questName.replace("_", " ");
					Quest quest = Quest.forName(questName);
					QuestObjective[] objectives = quest.getObjectives();
					for (QuestObjective objective : objectives) {
						objective.setProgress(pc, objective.getGoal());
					}
				}
				return true;
			}
		}
		return false;
	}

}
