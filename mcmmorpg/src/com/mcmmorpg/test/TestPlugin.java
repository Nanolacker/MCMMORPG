package com.mcmmorpg.test;

import java.io.File;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.EventManager;
import com.mcmmorpg.common.utils.IOUtils;

public class TestPlugin extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

		File fighterFile = new File(
				"C:\\Users\\conno\\git\\MCMMORPG\\mcmmorpg\\src\\com\\mcmmorpg\\test\\Fighter.json");
		PlayerClass fighter = IOUtils.jsonFromFile(fighterFile, PlayerClass.class);
		fighter.initialize();

		EventManager.registerEvents(new PCListener());
		QuestObjective o1 = new QuestObjective(10, "Kill 10 sheep");
		QuestObjective o2 = new QuestObjective(5, "Collect 5 flowers");
		QuestObjective[] objectives = { o1, o2 };
		Quest q = new Quest("Saving the Farm", 1, objectives);
		q.initialize();
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

}
