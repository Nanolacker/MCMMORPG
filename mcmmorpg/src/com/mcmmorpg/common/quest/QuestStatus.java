package com.mcmmorpg.common.quest;

/**
 * Stores a player's progress on a certain quest.
 */
public class QuestStatus {

	private final String questName;
	private final int[] objectiveData;

	QuestStatus(Quest quest) {
		this.questName = quest.getName();
		objectiveData = new int[quest.getObjectives().length];
		// initial data for each objective is 0
	}

	Quest getQuest() {
		return Quest.forName(questName);
	}

	int getProgress(QuestObjective objective) {
		return objectiveData[objective.getIndex()];
	}

	void setProgress(QuestObjective objective, int progress) {
		objectiveData[objective.getIndex()] = progress;
	}

}
