package com.mcmmorpg.impl;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.impl.resourceLoad.ItemLoader;
import com.mcmmorpg.impl.resourceLoad.ListenerLoader;
import com.mcmmorpg.impl.resourceLoad.PlayerClassLoader;
import com.mcmmorpg.impl.resourceLoad.QuestLoader;
import com.mcmmorpg.test.DeveloperCommands;

/**
 * Main class of the implementation of MCMMORPG.
 */
public class Main extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		load();
	}

	/**
	 * Loads resources from this plugin's data folder, registers events, and
	 * registers commands.
	 */
	private void load() {
		QuestLoader.loadQuests();
		PlayerClassLoader.loadClasses();
		ItemLoader.loadItems();
		ListenerLoader.loadListeners();
		DeveloperCommands.registerDeveloperCommands();
	}

	@Override
	protected void onMMORPGStop() {
	}

}
