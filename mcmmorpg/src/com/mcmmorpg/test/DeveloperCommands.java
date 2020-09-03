package com.mcmmorpg.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.ai.PathNode;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.Command;
import com.mcmmorpg.common.ui.CommandManager;
import com.mcmmorpg.common.util.CardinalDirection;
import com.mcmmorpg.common.util.Debug;
import com.mcmmorpg.common.util.Head;
import com.mcmmorpg.common.util.IOUtility;
import com.mcmmorpg.common.util.StringUtility;

/**
 * Class for registering commands useful for development.
 */
public class DeveloperCommands {

	private static final Map<String, Location> LOCATION_MAP = new HashMap<>();

	/**
	 * Registers commands useful for development.
	 */
	public static void registerDeveloperCommands() {
		Debug.log("Registering developer commands");

		Command printLocation = new Command("printlocation") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
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
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				double healAmount = Double.parseDouble(args[0]);
				pc.heal(healAmount, pc);
			}
		};
		Command restoreMana = new Command("restoremana") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				double manaAmount = Double.parseDouble(args[0]);
				pc.setCurrentMana(pc.getCurrentHealth() + manaAmount);
			}
		};
		Command giveXp = new Command("givexp") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				int xpAmount = Integer.parseInt(args[0]);
				pc.giveXp(xpAmount);
			}
		};
		Command startQuest = new Command("startquest") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
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
			protected void execute(CommandSender sender, String[] args) {
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
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String sAmount = args[args.length - 1];
				boolean lastTokenIsNumeric = StringUtility.isNumeric(sAmount);
				int amount;
				int lastItemTokenIndex;
				if (lastTokenIsNumeric) {
					amount = Integer.parseInt(sAmount);
					lastItemTokenIndex = args.length - 2;
				} else {
					amount = 1;
					lastItemTokenIndex = args.length - 1;
				}
				String itemName = args[0];
				for (int i = 1; i <= lastItemTokenIndex; i++) {
					itemName += " " + args[i];
				}
				Item item = Item.forName(itemName);
				pc.giveItem(item, amount);
			}
		};
		Command removeItem = new Command("removeitem") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String sAmount = args[args.length - 1];
				boolean lastTokenIsNumeric = StringUtility.isNumeric(sAmount);
				int amount;
				int lastItemTokenIndex;
				if (lastTokenIsNumeric) {
					amount = Integer.parseInt(sAmount);
					lastItemTokenIndex = args.length - 2;
				} else {
					amount = 1;
					lastItemTokenIndex = args.length - 1;
				}
				String itemName = args[0];
				for (int i = 1; i <= lastItemTokenIndex; i++) {
					itemName += " " + args[i];
				}
				Item item = Item.forName(itemName);
				pc.removeItem(item, amount);
			}
		};
		Command saveLocation = new Command("savelocation") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				Location location = player.getLocation();
				File file = new File("C:/Users/conno/Desktop/Locations.txt");
				if (!file.exists()) {
					IOUtility.createFile(file);
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
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				Location location = player.getLocation();
				Debug.log(CardinalDirection.forVector(location.getDirection()));
			}
		};
		Command removeEntities = new Command("removeentities") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
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
			protected void execute(CommandSender sender, String[] args) {
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
		Command goTo = new Command("goto") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				String locationName = args[0];
				for (int i = 1; i < args.length; i++) {
					locationName += " " + args[i];
				}
				Location location = LOCATION_MAP.get(locationName);
				pc.setLocation(location);
			}
		};
		Command spawnHead = new Command("spawnhead") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Location location = ((Player) sender).getLocation();

				Head head1 = new Head(sender.getName(), location);
				head1.setVisible(true);

				Head head2 = new Head(sender.getName(), location);
				head2.setVisible(true);

				new RepeatingTask(0.05) {
					@Override
					protected void run() {
						Location location = ((Player) sender).getLocation().clone().add(0, 1.4, 0);
						Vector lookDir = location.getDirection();
						Vector offset = new Vector(lookDir.getX(), 0, lookDir.getZ()).normalize()
								.rotateAroundY(Math.PI / 2).multiply(0.6);

						Location head1Loc = location.clone().add(offset);
						Location head2Loc = location.clone().subtract(offset);

						head1.setLocation(head1Loc);
						head2.setLocation(head2Loc);
					}
				}.schedule();
			}
		};
		Command sphere = new Command("particle") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
			}
		};
		Command node = new Command("node") {
			@Override
			protected void execute(CommandSender sender, String[] args) {
				Player player = (Player) sender;
				new AiTestNpc(player.getLocation()).setAlive(true);
			}
		};

		CommandManager.registerCommand(printLocation);
		CommandManager.registerCommand(heal);
		CommandManager.registerCommand(restoreMana);
		CommandManager.registerCommand(giveXp);
		CommandManager.registerCommand(startQuest);
		CommandManager.registerCommand(completeQuest);
		CommandManager.registerCommand(giveItem);
		CommandManager.registerCommand(removeItem);
		CommandManager.registerCommand(saveLocation);
		CommandManager.registerCommand(direction);
		CommandManager.registerCommand(removeEntities);
		CommandManager.registerCommand(reloadmmorpg);
		CommandManager.registerCommand(goTo);
		CommandManager.registerCommand(spawnHead);
		CommandManager.registerCommand(sphere);
		CommandManager.registerCommand(node);
	}

}
