package com.mcmmorpg.test;

import java.net.URL;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.JsonUtils;
import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.quest.Quest;

public class Test extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");
		URL resource = getClass().getResource("TestQuest.json");
		Quest quest = JsonUtils.jsonFromResource(resource, Quest.class);
		Debug.log(quest.getName());
	}

	@Override
	protected void onMMORPGStop() {
		// Debug.log("Stopping");
	}

}
