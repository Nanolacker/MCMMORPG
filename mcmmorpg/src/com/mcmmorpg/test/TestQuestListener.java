package com.mcmmorpg.test;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.utils.JsonUtils;

public class TestQuestListener {

	private final Quest quest;

	public TestQuestListener() {
		quest = JsonUtils.jsonFromFile(null, Quest.class);
		quest.initialize();
	}

	@EventHandler
	private void onCompleteObjectiveOne(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		QuestObjective objective = quest.getObjective(0, 0, 0);
		objective.addProgress(pc, 1);
	}

}
