package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * A quest can be made available to a player at anytime with makeAvailable. Once
 * a quest has been available, there is something that starts it, such as
 * speaking with an NPC or entering a new area. After being started,
 */
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
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Cannot initialize a quest after the plugin has been initialized.");
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
		PlayerQuestManager questManager = pc.getQuestManager();
		questManager.startQuest(this);
		pc.sendMessage("Quest started: " + name);
	}

}
