package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.impl.npcs.Bandit;

public class FlintonListener implements Listener {

	public FlintonListener() {
		setUpBounds();
		spawnNpcs();
	}

	private void setUpBounds() {

	}

	private void spawnNpcs() {
		Location[] banditLocations = {};
		for (Location location : banditLocations) {
			new Bandit(5, location).setAlive(true);
		}
	}

}
