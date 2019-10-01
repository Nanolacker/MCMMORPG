package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

public class QuestStatusManager {

	private final Map<Quest, QuestStatus> statusMap;

	public QuestStatusManager(QuestStatus[] questStatuses) {
		statusMap = new HashMap<>();
		for (QuestStatus questStatus : questStatuses) {
			Quest quest = questStatus.getQuest();
			statusMap.put(quest, questStatus);
		}
	}

	/**
	 * Used for serialization.
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

	void makeQuestAvailable(Quest quest) {
		QuestStatus status = new QuestStatus(quest);
		statusMap.put(quest, status);
	}

}
