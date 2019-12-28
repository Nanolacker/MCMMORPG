package com.mcmmorpg.impl;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.utils.Debug;

public class Main extends MMORPGPlugin {

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
