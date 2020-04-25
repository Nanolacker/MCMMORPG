package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.Quests;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.Horse;
import com.mcmmorpg.impl.npcs.MelcherFarmer;
import com.mcmmorpg.impl.npcs.MelcherLumberjack;
import com.mcmmorpg.impl.npcs.MelcherVillager;
import com.mcmmorpg.impl.npcs.Thief;
import com.mcmmorpg.impl.npcs.TrainingDummy;
import com.mcmmorpg.impl.npcs.WildBoar;

public class MelcherListener implements Listener {

	public MelcherListener() {
		setUpBounds();
		setUpNpcs();
	}

	private void setUpBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, -1186, 68, 110, -930, 126, 300) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(ChatColor.GREEN + "Melcher");
				}
			}
		};
		entranceBounds.setActive(true);
	}

	private void setUpNpcs() {
		spawnVillagers();
		spawnLumberjacks();
		spawnFarmer();
		spawnTrainingDummies();
		spawnChickens();
		spawnHorses();
		spawnThieves();
		spawnWildBoars();
	}

	private void spawnVillagers() {
		Location[] villagerLocations = { new Location(Worlds.ELADRADOR, -1167, 73, 273),
				new Location(Worlds.ELADRADOR, -1153, 73, 269), new Location(Worlds.ELADRADOR, -1138, 73, 260),
				new Location(Worlds.ELADRADOR, -1146, 73, 240), new Location(Worlds.ELADRADOR, -1130, 73, 242),
				new Location(Worlds.ELADRADOR, -1113, 72, 231), new Location(Worlds.ELADRADOR, -1100, 70, 218),
				new Location(Worlds.ELADRADOR, -1086, 70, 233), new Location(Worlds.ELADRADOR, -1073, 70, 211),
				new Location(Worlds.ELADRADOR, -1058, 70, 228), new Location(Worlds.ELADRADOR, -1037, 70, 233),
				new Location(Worlds.ELADRADOR, -1025, 70, 207), new Location(Worlds.ELADRADOR, -1030, 70, 185),
				new Location(Worlds.ELADRADOR, -1032, 70, 170), new Location(Worlds.ELADRADOR, -1013, 70, 181),
				new Location(Worlds.ELADRADOR, -998, 70, 208), new Location(Worlds.ELADRADOR, -988, 71, 181),
				new Location(Worlds.ELADRADOR, -969, 71, 177), new Location(Worlds.ELADRADOR, -957, 72, 157),
				new Location(Worlds.ELADRADOR, -929, 72, 155) };
		for (int i = 0; i < villagerLocations.length; i++) {
			Location location = villagerLocations[i];
			boolean male = i % 2 == 0;
			new MelcherVillager(location, male).setAlive(true);
		}
	}

	private void spawnLumberjacks() {
		Location[] lumberjackLocations = { new Location(Worlds.ELADRADOR, -1049, 70, 232, 225, 0) };
		for (Location location : lumberjackLocations) {
			new MelcherLumberjack(location).setAlive(true);
		}
	}

	private void spawnFarmer() {
		Location farmerLocation = new Location(Worlds.ELADRADOR, -1166, 73, 246);
		new MelcherFarmer(farmerLocation).setAlive(true);
	}

	private void spawnTrainingDummies() {
		Location[] trainingDummyLocations = { new Location(Worlds.ELADRADOR, -1115, 73, 249, 90, 0),
				new Location(Worlds.ELADRADOR, -1117, 73, 251, 90, 0),
				new Location(Worlds.ELADRADOR, -1116, 73, 255, -45, 0),
				new Location(Worlds.ELADRADOR, -1114, 73, 252, 90, 0),
				new Location(Worlds.ELADRADOR, -1118, 73, 248, 90, 0),
				new Location(Worlds.ELADRADOR, -1119, 73, 256, 180, 0) };
		for (Location location : trainingDummyLocations) {
			new TrainingDummy(location).setAlive(true);
		}
	}

	private void spawnChickens() {
		Location[] chickenLocations = { new Location(Worlds.ELADRADOR, -1167, 73, 243),
				new Location(Worlds.ELADRADOR, -1167, 73, 242), new Location(Worlds.ELADRADOR, -1167, 73, 241) };
		for (Location location : chickenLocations) {
			new Chicken(location).setAlive(true);
		}
	}

	private void spawnHorses() {
		Location[] horseLocations = { new Location(Worlds.ELADRADOR, -1158, 73, 239),
				new Location(Worlds.ELADRADOR, -1158, 73, 244), new Location(Worlds.ELADRADOR, -1158, 73, 249) };
		for (Location location : horseLocations) {
			new Horse(ChatColor.GREEN + "Horse", 3, location).setAlive(true);
		}
	}

	private void spawnThieves() {
		Location[] thiefLocations = {};
		for (Location location : thiefLocations) {
			new Thief(location).setAlive(true);
		}
	}

	private void spawnWildBoars() {
		Location[] wildBoarLocations = { new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160) };
		for (Location location : wildBoarLocations) {
			new WildBoar(3, location).setAlive(true);
		}
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		int level = event.getNewLevel();
		if (level == 1) {
			PlayerCharacter pc = event.getPlayerCharacter();
			Quests.REPORTING_FOR_DUTY.start(pc);
		}
	}

}
