package com.mcmmorpg.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.Command;
import com.mcmmorpg.common.ui.CommandManager;
import com.mcmmorpg.common.utils.CardinalDirection;
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
		Command direction = new Command("direction") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				Location location = player.getLocation();
				Debug.log(CardinalDirection.forVector(location.getDirection()));
			}
		};
		Command removeEntities = new Command("removeentities") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				for (World world : Bukkit.getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity != sender) {
							entity.remove();
						}
					}
				}
			}
		};
		Command reloadmmorpg = new Command("reloadmmorpg") {
			@Override
			protected void onExecute(CommandSender sender, String[] args) {
				Debug.log("reloading...");
				List<PlayerCharacter> pcs = new ArrayList<>(PlayerCharacter.listAll());
				for (PlayerCharacter pc : pcs) {
					pc.remove();
				}
				new DelayedTask(1.5) {
					@Override
					protected void run() {
						Bukkit.getServer().reload();
					}
				}.schedule();
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
		CommandManager.registerCommand(direction);
		CommandManager.registerCommand(removeEntities);
		CommandManager.registerCommand(reloadmmorpg);
	}

}
