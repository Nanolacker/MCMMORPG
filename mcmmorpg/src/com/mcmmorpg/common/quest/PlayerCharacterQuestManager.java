package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages data for a player character's quests.
 */
public class PlayerCharacterQuestManager {

	private final List<Quest> completedQuests;
	private final Map<Quest, PlayerCharacterQuestData> questDataMap;

	public PlayerCharacterQuestManager(Quest[] completedQuests, PlayerCharacterQuestData[] questDatas) {
		this.completedQuests = new ArrayList<>(Arrays.asList(completedQuests));
		questDataMap = new HashMap<>();
		for (PlayerCharacterQuestData questData : questDatas) {
			Quest quest = Quest.forName(questData.getQuestName());
			questDataMap.put(quest, questData);
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
	public PlayerCharacterQuestData[] getQuestData() {
		return questDataMap.values().toArray(new PlayerCharacterQuestData[questDataMap.size()]);
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
	PlayerCharacterQuestData getQuestData(Quest quest) {
		return questDataMap.get(quest);
	}

	void startQuest(Quest quest) {
		PlayerCharacterQuestData data = new PlayerCharacterQuestData(quest);
		questDataMap.put(quest, data);
	}

	void completeQuest(Quest quest) {
		completedQuests.add(quest);
		questDataMap.remove(quest);
	}

}
