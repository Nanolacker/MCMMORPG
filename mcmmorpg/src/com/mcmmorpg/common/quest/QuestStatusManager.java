package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestStatusManager {

	private List<Quest> availableQuests;
	private final Map<Quest, QuestStatus> statusMap;

	public QuestStatusManager(String[] availableQuestNames, QuestStatus[] questStatuses) {
		availableQuests = new ArrayList<>();
		for (String questName : availableQuestNames) {
			Quest quest = Quest.forName(questName);
			availableQuests.add(quest);
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
	public String[] getAvailableQuestNames() {
		int size = availableQuests.size();
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			Quest quest = availableQuests.get(i);
			names[i] = quest.getName();
		}
		return names;
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

	boolean questIsAvailable(Quest quest) {
		return availableQuests.contains(quest);
	}

	void makeQuestAvailable(Quest quest) {
		availableQuests.add(quest);
	}

	public void startQuest(Quest quest) {
		QuestStatus status = new QuestStatus(quest);
		statusMap.put(quest, status);
	}
	
	public boolean isStarted(Quest quest) {
		return statusMap.containsKey(quest);
	}

}
