package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.TrainingDummy;
import com.mcmmorpg.impl.npcs.TutorialGuide;
import com.mcmmorpg.impl.npcs.TutorialWeaponsDealer;

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
		TutorialGuide tutorialGuide = new TutorialGuide(tutorialGuideLocation);
		tutorialGuide.setAlive(true);

		Location weaponsDealerLocation = new Location(Worlds.ELADRADOR, -486, 66, -130);
		TutorialWeaponsDealer weaponsDealer = new TutorialWeaponsDealer(weaponsDealerLocation);
		weaponsDealer.setAlive(true);

		Location[] trainingDummyLocations = { new Location(Worlds.ELADRADOR, -482, 66, -135),
				new Location(Worlds.ELADRADOR, 479, 66, -131, 5, 13), new Location(Worlds.ELADRADOR, -488, 66, -133),
				new Location(Worlds.ELADRADOR, -488, 66, -129), new Location(Worlds.ELADRADOR, -471, 66, -131),
				new Location(Worlds.ELADRADOR, -486, 65, -144), new Location(Worlds.ELADRADOR, -495, 65, -137) };
		for (Location location : trainingDummyLocations) {
			new TrainingDummy(location).setAlive(true);
		}
	}

}
