package com.mcmmorpg.impl.resourceLoad;

import java.io.File;

import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Class for loading quests from the plugin's data folder.
 */
public class QuestLoader {

	/**
	 * Loads quests from the plugin's data folder.
	 */
	public static void loadQuests() {
		File questFolder = new File(IOUtility.getDataFolder(), "resources/quests");
		File[] questFiles = questFolder.listFiles();
		for (File questFile : questFiles) {
			Quest quest = IOUtility.readJson(questFile, Quest.class);
			quest.initialize();
		}
	}

}
