package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestCompletionEvent;

public class Quest {

	private static final Map<String, Quest> quests;

	static {
		quests = new HashMap<>();
	}

	private final String name;
	private final int recommendedLevel;
	private final QuestObjective[] objectives;

	public Quest(String name, int recommendedLevel, QuestObjective[] objectives) {
		this.name = name;
		this.recommendedLevel = recommendedLevel;
		this.objectives = objectives;
	}

	public void initialize() {
		for (int i = 0; i < objectives.length; i++) {
			QuestObjective objective = objectives[i];
			objective.initialize(this, i);
		}
		quests.put(name, this);
	}

	public static Quest forName(String name) {
		return quests.get(name);
	}

	public String getName() {
		return name;
	}

	public int getRecommendedLevel() {
		return recommendedLevel;
	}

	public QuestObjective[] getObjectives() {
		return objectives;
	}

	public QuestStatus getStatus(PlayerCharacter pc) {
		return pc.getQuestManager().getStatus(this);
	}

	public void start(PlayerCharacter pc) {
		if (getStatus(pc) != QuestStatus.NOT_STARTED) {
			throw new IllegalArgumentException("Player has already started quest");
		}
		PlayerQuestManager questManager = pc.getQuestManager();
		questManager.startQuest(this);
		pc.sendMessage("Quest started: " + name);
	}

	void checkForCompletion(PlayerCharacter pc) {
		for (QuestObjective objective : objectives) {
			if (!objective.isComplete(pc)) {
				return;
			}
		}
		complete(pc);
	}

	private void complete(PlayerCharacter pc) {
		pc.sendMessage("Quest complete: " + this.name);
		if (pc.getTargetQuest() == this) {
			pc.setTargetQuest(null);
		}
		PlayerQuestManager questManager = pc.getQuestManager();
		questManager.completeQuest(this);
		QuestCompletionEvent event = new QuestCompletionEvent(pc, this);
		EventManager.callEvent(event);
	}

}
