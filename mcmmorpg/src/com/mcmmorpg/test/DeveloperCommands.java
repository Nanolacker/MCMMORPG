package com.mcmmorpg.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.ui.Command;
import com.mcmmorpg.common.ui.CommandManager;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;

public class DeveloperCommands {

	public static void registerDeveloperCommands() {
		Debug.log("Registering developer commands");

		Command printLocation = new Command("printlocation") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				Location location = player.getLocation();
				int x = (int) location.getX();
				int y = (int) location.getY();
				int z = (int) location.getZ();
				int yaw = (int) location.getYaw();
				int pitch = (int) location.getPitch();
				Debug.log(x + ", " + y + ", " + z + ", " + yaw + ", " + pitch);
			}
		};
		Command heal = new Command("heal") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				double healAmount = Double.parseDouble(args[0]);
				pc.heal(healAmount, pc);
			}
		};
		Command restoreMana = new Command("restoremana") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				double manaAmount = Double.parseDouble(args[0]);
				pc.setCurrentMana(pc.getCurrentHealth() + manaAmount);
			}
		};
		Command giveXp = new Command("givexp") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				int xpAmount = Integer.parseInt(args[0]);
				pc.giveXp(xpAmount);
			}
		};
		Command startQuest = new Command("startquest") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String questName = args[0];
				for (int i = 1; i < args.length; i++) {
					questName += " " + args[i];
				}
				Quest quest = Quest.forName(questName);
				quest.start(pc);
			}
		};
		Command completeQuest = new Command("completequest") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String questName = args[0];
				for (int i = 1; i < args.length; i++) {
					questName += " " + args[i];
				}
				Quest quest = Quest.forName(questName);
				QuestObjective[] objectives = quest.getObjectives();
				for (QuestObjective objective : objectives) {
					objective.complete(pc);
				}
			}
		};
		Command giveItem = new Command("giveitem") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String itemName = args[0];
				for (int i = 1; i < args.length; i++) {
					itemName += " " + args[i];
				}
				Item item = Item.forName(itemName);
				pc.giveItem(item);
			}
		};
		Command saveLocation = new Command("savelocation") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				Location location = player.getLocation();
				File file = new File("C:\\Users\\conno\\Desktop\\Locations.txt");
				if (!file.exists()) {
					IOUtils.createFile(file);
				}

				try {
					FileWriter fileWriter = new FileWriter(file, true);
					fileWriter.append(String.format("new Location(Worlds.ELADRADOR, %f, %f, %f, %ff, %ff),\n",
							location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
					Debug.log("saved " + location);
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		CommandManager.registerCommand(printLocation);
		CommandManager.registerCommand(heal);
		CommandManager.registerCommand(restoreMana);
		CommandManager.registerCommand(giveXp);
		CommandManager.registerCommand(startQuest);
		CommandManager.registerCommand(completeQuest);
		CommandManager.registerCommand(giveItem);
		CommandManager.registerCommand(saveLocation);
	}

}
