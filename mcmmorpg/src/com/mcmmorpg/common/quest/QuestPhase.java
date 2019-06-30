package com.mcmmorpg.common.quest;

public class QuestPhase {

	private final QuestObjectiveChain[] objectiveChains;
	private transient Quest quest;
	private transient int index;

	public QuestPhase(QuestObjectiveChain[] objectiveChains) {
		this.objectiveChains = objectiveChains;
	}

	void initialize(Quest quest, int index) {
		this.quest = quest;
		this.index = index;
		for (int i = 0; i < objectiveChains.length; i++) {
			QuestObjectiveChain objectiveChain = objectiveChains[i];
			objectiveChain.initialize(this, i);
		}
	}

	public QuestObjectiveChain[] getObjectiveChains() {
		return objectiveChains;
	}

	public Quest getQuest() {
		return quest;
	}

	public int getIndex() {
		return index;
	}

}
