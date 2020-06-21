package com.mcmmorpg.impl.load;

import java.io.File;

import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Class for loading player classes from the plugin's data folder. The listeners
 * for the player classes are handled elsewhere.
 */
public class PlayerClassLoader {

	/**
	 * Loads player classes from the plugin's data folder. The listeners for the
	 * player classes are handled elsewhere.
	 */
	public static void loadClasses() {
		File playerClassFolder = new File(IOUtility.getDataFolder(), "resources/playerClasses");
		File[] playerClassFiles = playerClassFolder.listFiles();
		for (File file : playerClassFiles) {
			PlayerClass playerClass = IOUtility.readJsonFile(file, PlayerClass.class);
			playerClass.initialize();
		}
	}

}
