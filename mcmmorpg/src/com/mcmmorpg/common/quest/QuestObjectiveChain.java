package com.mcmmorpg.common.quest;

public class QuestObjectiveChain {

	private final QuestObjective[] objectives;
	private transient QuestPhase phase;
	private transient int index;

	public QuestObjectiveChain(QuestObjective[] objectives) {
		this.objectives = objectives;
	}

	void initialize(QuestPhase phase, int index) {
		this.phase = phase;
		this.index = index;
		for (int i = 0; i < objectives.length; i++) {
			QuestObjective objective = objectives[i];
			objective.initialize(this, i);
		}
	}

	public QuestObjective[] getObjectives() {
		return objectives;
	}

	public QuestPhase getPhase() {
		return phase;
	}

	public int getIndex() {
		return index;
	}
}
