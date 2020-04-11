package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.impl.Quests;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BanditQuestGiver;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.FoodQuestGiver;
import com.mcmmorpg.impl.npcs.MelcherResident;
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
		Location banditQuestGiverLocation = new Location(Worlds.ELADRADOR, -478, 67, -117);
		BanditQuestGiver banditQuestGiver = new BanditQuestGiver(banditQuestGiverLocation);
		banditQuestGiver.setAlive(true);

		Location foodQuestGiverLocation = new Location(Worlds.ELADRADOR, -485, 67, -117);
		FoodQuestGiver foodQuestGiver = new FoodQuestGiver(foodQuestGiverLocation);
		foodQuestGiver.setAlive(true);

		Location[] residentLocations = {};
		for (Location location : residentLocations) {
			new MelcherResident(location).setAlive(true);
		}

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
			new Chicken(location).setAlive(true);
			// new Thief(3, location).setAlive(true);
		}

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
