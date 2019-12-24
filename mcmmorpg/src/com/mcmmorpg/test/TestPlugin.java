package com.mcmmorpg.test;

import java.io.File;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;

public class TestPlugin extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

		File fighterFile = new File(
				"C:\\Users\\conno\\git\\MCMMORPG\\mcmmorpg\\src\\com\\mcmmorpg\\test\\Fighter.json");
		PlayerClass fighter = IOUtils.jsonFromFile(fighterFile, PlayerClass.class);
		fighter.initialize();

		registerEvents(new PCListener());
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

}
