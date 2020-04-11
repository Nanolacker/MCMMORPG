package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.impl.npcs.Bandit;

public class OakshireListener implements Listener {

	public OakshireListener() {
		setUpBounds();
		spawnNpcs();
	}

	private void setUpBounds() {

	}

	private void spawnNpcs() {
		Location[] banditLocations = {};
		for (Location location : banditLocations) {
			new Bandit(location).setAlive(true);
		}
	}

}
