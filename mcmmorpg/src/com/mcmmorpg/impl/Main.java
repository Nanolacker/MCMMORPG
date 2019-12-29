package com.mcmmorpg.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.utils.Debug;

public class Main extends MMORPGPlugin {

	public static final World ELADRADOR = Bukkit.getWorld("Eladrador");

	@Override
	protected void onMMORPGStart() {
		Debug.log("starting");
		load();
	}

	private void load() {
		ItemLoader.loadItems();
		QuestLoader.loadQuests();
		ZoneLoader.loadZones();
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("stopping");
	}

}
