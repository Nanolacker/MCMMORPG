package com.mcmmorpg.test;

import org.bukkit.Location;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.MMORPGPlugin;

public class Test extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

		Location location = null;
		Monster monster = new Monster(location);
		monster.setSpawning(true);
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

}
