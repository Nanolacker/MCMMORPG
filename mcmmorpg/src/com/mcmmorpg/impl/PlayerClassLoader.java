package com.mcmmorpg.impl;

import java.io.File;

import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.IOUtils;

public class PlayerClassLoader {

	public static void loadClasses() {
		File playerClassFolder = new File(IOUtils.getDataFolder(), "player_classes");
		File[] playerClassFiles = playerClassFolder.listFiles();
		for (File file : playerClassFiles) {
			PlayerClass playerClass = IOUtils.jsonFromFile(file, PlayerClass.class);
			playerClass.initialize();
		}
	}

}
