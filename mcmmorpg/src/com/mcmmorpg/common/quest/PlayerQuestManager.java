package com.mcmmorpg.common.quest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerQuestManager {

	private final List<Quest> completedQuests;
	private final Map<Quest, PlayerQuestData> questDataMap;

	public PlayerQuestManager(Quest[] completedQuests, PlayerQuestData[] questData) {
		this.completedQuests = Arrays.asList(completedQuests);
		questDataMap = new HashMap<>();
		for (PlayerQuestData questStatus : questData) {
			Quest quest = Quest.forName(questStatus.getQuestName());
			questDataMap.put(quest, questStatus);
		}
	}

	/**
	 * Used for saving player data.
	 */
	public List<Quest> getCompletedQuests() {
		return completedQuests;
	}

	/**
	 * Used for saving player data.
	 */
	public PlayerQuestData[] getQuestData() {
		return questDataMap.values().toArray(new PlayerQuestData[questDataMap.size()]);
	}

	QuestStatus getStatus(Quest quest) {
		if (completedQuests.contains(quest)) {
			return QuestStatus.COMPLETED;
		} else if (questDataMap.containsKey(quest)) {
			return QuestStatus.IN_PROGRESS;
		} else {
			return QuestStatus.NOT_STARTED;
		}
	}

	/**
	 * Returns null if the specified quest is not available to the player.
	 */
	PlayerQuestData getQuestData(Quest quest) {
		return questDataMap.get(quest);
	}

	void startQuest(Quest quest) {
		PlayerQuestData status = new PlayerQuestData(quest);
		questDataMap.put(quest, status);
	}

}
