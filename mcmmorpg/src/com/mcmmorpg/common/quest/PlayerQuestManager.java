package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerQuestManager {

	private final List<Quest> completedQuests;
	private final Map<Quest, QuestStatus> statusMap;

	public PlayerQuestManager(String[] completedQuestNames, QuestStatus[] questStatuses) {
		completedQuests = new ArrayList<>();
		for (String questName : completedQuestNames) {
			completedQuests.add(Quest.forName(questName));
		}
		statusMap = new HashMap<>();
		for (QuestStatus questStatus : questStatuses) {
			Quest quest = questStatus.getQuest();
			statusMap.put(quest, questStatus);
		}
	}

	/**
	 * Used for saving player data.
	 */
	public QuestStatus[] getQuestStatuses() {
		return statusMap.values().toArray(new QuestStatus[statusMap.size()]);
	}

	/**
	 * Returns null if the specified quest is not available to the player.
	 */
	QuestStatus getQuestStatus(Quest quest) {
		return statusMap.get(quest);
	}

	void startQuest(Quest quest) {
		QuestStatus status = new QuestStatus(quest);
		statusMap.put(quest, status);
	}

	boolean isStarted(Quest quest) {
		return statusMap.containsKey(quest);
	}

}
