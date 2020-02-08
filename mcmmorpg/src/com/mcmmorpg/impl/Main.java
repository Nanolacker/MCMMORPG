package com.mcmmorpg.impl;

import com.mcmmorpg.common.MMORPGPlugin;

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
	}

	@Override
	protected void onMMORPGStop() {
	}

}
