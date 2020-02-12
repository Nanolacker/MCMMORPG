package com.mcmmorpg.common.quest;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestObjectiveChangeProgressEvent;
import com.mcmmorpg.common.utils.MathUtils;

public class QuestObjective {

	private final int goal;
	private final String description;
	private transient Quest quest;
	private transient int index;

	public QuestObjective(int goal, String description) {
		this.goal = goal;
		this.description = description;
	}

	void initialize(Quest quest, int index) {
		this.quest = quest;
		this.index = index;
	}

	/**
	 * Returns the goal progress of this objective. Upon reaching this goal, this
	 * objective will be considered complete for a player character.
	 */
	public int getGoal() {
		return goal;
	}

	/**
	 * Returns the description of this objective.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the quest that this objective is a part of.
	 */
	public Quest getQuest() {
		return quest;
	}

	/**
	 * Returns the placement of this objective in the quest's objective list.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the progress of the player character on this objective. If the player
	 * has not started this quest or has already completed it, an exception is
	 * thrown.
	 */
	public int getProgress(PlayerCharacter pc) {
		PlayerQuestManager statusManager = pc.getQuestManager();
		PlayerQuestData data = statusManager.getQuestData(quest);
		if (data == null) {
			throw new IllegalArgumentException("Quest not in progress for player character");
		}
		return data.getProgress(this.index);
	}

	/**
	 * Sets the progress of the player character on this objective. If the player
	 * has not started this quest or has already completed it, an exception is
	 * thrown.
	 */
	public void setProgress(PlayerCharacter pc, int progress) {
		PlayerQuestManager questManager = pc.getQuestManager();
		PlayerQuestData data = questManager.getQuestData(quest);
		if (data == null) {
			throw new IllegalArgumentException("Quest not in progress for player character");
		}
		int previousProgress = getProgress(pc);
		progress = (int) MathUtils.clamp(progress, 0, goal);
		data.setProgress(this.index, progress);
		QuestObjectiveChangeProgressEvent event = new QuestObjectiveChangeProgressEvent(pc, this, previousProgress,
				progress);
		EventManager.callEvent(event);
		if (pc.getTargetQuest() == this.quest) {
			pc.updateQuestDisplay();
		}
		quest.checkForCompletion(pc);
	}

	/**
	 * Equivalent to setProgress(getProgress() + progressToAdd). Negative progress
	 * to add will remove progress.If the player has not started this quest or has
	 * already completed it, an exception is thrown.
	 */
	public void addProgress(PlayerCharacter pc, int progressToAdd) {
		int progress = getProgress(pc);
		setProgress(pc, progress + progressToAdd);
	}

	/**
	 * Returns whether the player character has completed this objective. Returns
	 * true if the player character has finished this quest or false if the player
	 * character has not started this quest.
	 */
	public boolean isComplete(PlayerCharacter pc) {
		QuestStatus status = quest.getStatus(pc);
		switch (status) {
		case NOT_STARTED:
			return false;
		case IN_PROGRESS:
			return getProgress(pc) == this.goal;
		case COMPLETED:
			return true;
		default:
			return false;
		}
	}

}
