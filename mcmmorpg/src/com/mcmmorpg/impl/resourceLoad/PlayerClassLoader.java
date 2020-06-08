package com.mcmmorpg.impl.resourceLoad;

import java.io.File;

import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.util.IOUtility;

public class PlayerClassLoader {

	public static void loadClasses() {
		File playerClassFolder = new File(IOUtility.getDataFolder(), "resources/playerClasses");
		File[] playerClassFiles = playerClassFolder.listFiles();
		for (File file : playerClassFiles) {
			PlayerClass playerClass = IOUtility.readJson(file, PlayerClass.class);
			playerClass.initialize();
		}
	}

}
