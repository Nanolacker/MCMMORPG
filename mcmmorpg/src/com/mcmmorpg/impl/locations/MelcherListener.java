package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BanditQuestGiver;
import com.mcmmorpg.impl.npcs.CombatTrainer;
import com.mcmmorpg.impl.npcs.FoodQuestGiver;
import com.mcmmorpg.impl.npcs.GelatinousCube;
import com.mcmmorpg.impl.npcs.Thief;
import com.mcmmorpg.impl.npcs.TrainingDummy;
import com.mcmmorpg.impl.npcs.WildBoar;

public class MelcherListener implements Listener {

	public MelcherListener() {
		setUpBounds();
		setUpNpcs();
	}

	private void setUpBounds() {
		// do this later
	}

	private void setUpNpcs() {
		Location tutorialGuideLocation = new Location(Worlds.ELADRADOR, -484, 66, -130);
		CombatTrainer tutorialGuide = new CombatTrainer(tutorialGuideLocation);
		tutorialGuide.setAlive(true);

		Location banditQuestGiverLocation = new Location(Worlds.ELADRADOR, -478, 67, -117);
		BanditQuestGiver banditQuestGiver = new BanditQuestGiver(banditQuestGiverLocation);
		banditQuestGiver.setAlive(true);

		Location foodQuestGiverLocation = new Location(Worlds.ELADRADOR, -485, 67, -117);
		FoodQuestGiver foodQuestGiver = new FoodQuestGiver(foodQuestGiverLocation);
		foodQuestGiver.setAlive(true);

		Location[] trainingDummyLocations = { new Location(Worlds.ELADRADOR, -482, 66, -135),
				new Location(Worlds.ELADRADOR, 479, 66, -131, 5, 13), new Location(Worlds.ELADRADOR, -488, 66, -133),
				new Location(Worlds.ELADRADOR, -488, 66, -129), new Location(Worlds.ELADRADOR, -471, 66, -131),
				new Location(Worlds.ELADRADOR, -486, 65, -144), new Location(Worlds.ELADRADOR, -495, 65, -137) };
		for (Location location : trainingDummyLocations) {
			new TrainingDummy(location).setAlive(true);
		}

		Location[] thiefLocations = { new Location(Worlds.ELADRADOR, -441, 67, -117),
				new Location(Worlds.ELADRADOR, -441, 67, -117), new Location(Worlds.ELADRADOR, -441, 67, -117),
				new Location(Worlds.ELADRADOR, -441, 67, -117), new Location(Worlds.ELADRADOR, -441, 67, -117) };
		for (Location location : thiefLocations) {
			new Thief(3, location).setAlive(true);
		}

		Location[] wildBoarLocations = { new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160) };
		for (Location location : wildBoarLocations) {
			new WildBoar(3, location).setAlive(true);
		}

		new GelatinousCube(trainingDummyLocations[0]).setAlive(true);
	}

}
