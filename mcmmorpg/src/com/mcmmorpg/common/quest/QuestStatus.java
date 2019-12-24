package com.mcmmorpg.common.quest;

import java.io.Serializable;

/**
 * Stores a player's progress on a certain quest.
 */
public class QuestStatus implements Serializable {

	private final String questName;
	private final int[][][] progressData;

	QuestStatus(Quest quest) {
		questName = quest.getName();
		QuestPhase[] phases = quest.getPhases();
		int phaseCount = phases.length;
		progressData = new int[phaseCount][][];
		for (int i = 0; i < phaseCount; i++) {
			QuestPhase phase = phases[i];
			QuestObjectiveChain[] objectiveChains = phase.getObjectiveChains();
			int objectiveChainCount = objectiveChains.length;
			progressData[i] = new int[objectiveChainCount][];
			for (int j = 0; j < objectiveChainCount; j++) {
				QuestObjectiveChain objectiveChain = objectiveChains[j];
				QuestObjective[] objectives = objectiveChain.getObjectives();
				int objectiveCount = objectives.length;
				progressData[i][j] = new int[objectiveCount];
			}
		}
	}

	Quest getQuest() {
		return Quest.forName(questName);
	}

	int getProgress(QuestObjective objective) {
		QuestObjectiveChain objectiveChain = objective.getObjectiveChain();
		QuestPhase phase = objectiveChain.getPhase();
		int iPhase = phase.getIndex();
		int iObjectiveChain = objectiveChain.getIndex();
		int iObjective = objective.getIndex();
		return progressData[iPhase][iObjectiveChain][iObjective];
	}

	void setProgress(QuestObjective objective, int progress) {
		QuestObjectiveChain objectiveChain = objective.getObjectiveChain();
		QuestPhase phase = objectiveChain.getPhase();
		int iPhase = phase.getIndex();
		int iObjectiveChain = objectiveChain.getIndex();
		int iObjective = objective.getIndex();
		progressData[iPhase][iObjectiveChain][iObjective] = progress;
	}
	
}
