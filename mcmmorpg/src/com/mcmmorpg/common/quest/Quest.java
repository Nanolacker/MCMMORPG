package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestCompletionEvent;

public class Quest {

	private static final Map<String, Quest> quests;

	static {
		quests = new HashMap<>();
	}

	private final String name;
	private final int level;
	private final QuestObjective[] objectives;

	public Quest(String name, int level, QuestObjective[] objectives) {
		this.name = name;
		this.level = level;
		this.objectives = objectives;
	}

	public void initialize() {
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Quest must be initialized during initialization of plugin");
		}
		for (int i = 0; i < objectives.length; i++) {
			QuestObjective objective = objectives[i];
			objective.initialize(this, i);
		}
		quests.put(name, this);
	}

	public static Quest forName(String name) {
		return quests.get(name);
	}

	public static List<Quest> getAll() {
		return new ArrayList<Quest>(quests.values());
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public QuestObjective[] getObjectives() {
		return objectives;
	}

	public QuestObjective getObjective(int index) {
		return objectives[index];
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
		if (pc.getTargetQuest() == null) {
			pc.setTargetQuest(this);
		}
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
