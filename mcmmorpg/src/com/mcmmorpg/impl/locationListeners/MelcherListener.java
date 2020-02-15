package com.mcmmorpg.impl.locationListeners;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.MelcherFarmer;

public class MelcherListener implements Listener {

	public MelcherListener() {
		setupNpcs();
	}

	private void setupNpcs() {
		MelcherFarmer farmer = new MelcherFarmer(3, new Location(Worlds.ELADRADOR, 226, 65, 155));
		farmer.setAlive(true);
	}

}
