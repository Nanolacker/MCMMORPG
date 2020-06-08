package com.mcmmorpg.impl.resourceLoad;

import java.io.File;

import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Loads quest from the plugin's data folder. Assumes that there is a directory
 * named "quests", which contains files of quests in JSON format, in the data
 * folder.
 */
public class QuestLoader {

	public static void loadQuests() {
		File questFolder = new File(IOUtility.getDataFolder(), "resources/quests");
		File[] questFiles = questFolder.listFiles();
		for (File questFile : questFiles) {
			Quest quest = IOUtility.readJson(questFile, Quest.class);
			quest.initialize();
		}
	}

}
