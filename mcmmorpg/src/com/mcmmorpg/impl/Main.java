package com.mcmmorpg.impl;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.test.DeveloperCommands;

public class Main extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		load();
	}

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
