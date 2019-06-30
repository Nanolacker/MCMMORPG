package com.mcmmorpg.common.quest;

import com.mcmmorpg.common.character.PlayerCharacter;

public class QuestObjective {

	private final int goal;
	private final String description;
	private transient QuestObjectiveChain objectiveChain;
	private transient int index;

	public QuestObjective(int goal, String description) {
		this.goal = goal;
		this.description = description;
	}

	void initialize(QuestObjectiveChain objectiveChain, int index) {
		this.objectiveChain = objectiveChain;
		this.index = index;
	}

	public int getGoal() {
		return goal;
	}

	public String getDescription() {
		return description;
	}

	public QuestObjectiveChain getObjectiveChain() {
		return objectiveChain;
	}

	public int getIndex() {
		return index;
	}

	public int getProgress(PlayerCharacter pc) {
		QuestStatusManager statusManager = pc.getQuestStatusManager();
		Quest quest = objectiveChain.getPhase().getQuest();
		QuestStatus status = statusManager.getQuestStatus(quest);
		return status.getProgress(this);
	}

	public void setProgress(PlayerCharacter pc, int progress) {
		QuestStatusManager statusManager = pc.getQuestStatusManager();
		Quest quest = objectiveChain.getPhase().getQuest();
		QuestStatus status = statusManager.getQuestStatus(quest);
		status.setProgress(this, progress);
	}

	/**
	 * @param progressToAdd Progress can be reduced by specifying a negative number.
	 */
	public void addProgress(PlayerCharacter pc, int progressToAdd) {
		int progress = getProgress(pc);
		progress += progressToAdd;
		setProgress(pc, progress);
	}

}
