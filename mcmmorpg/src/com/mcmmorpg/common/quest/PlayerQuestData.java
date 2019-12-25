package com.mcmmorpg.common.quest;

/**
 * Stores a player's progress on a certain quest.
 */
public class PlayerQuestData {

	private final String questName;
	private final int[] objectiveData;

	PlayerQuestData(Quest quest) {
		this.questName = quest.getName();
		objectiveData = new int[quest.getObjectives().length];
		// initial progress for each objective is 0
	}

	String getQuestName() {
		return questName;
	}

	int getProgress(int objectiveIndex) {
		return objectiveData[objectiveIndex];
	}

	void setProgress(int objectiveIndex, int progress) {
		objectiveData[objectiveIndex] = progress;
	}

}
