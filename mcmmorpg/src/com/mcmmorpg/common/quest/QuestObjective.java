package com.mcmmorpg.common.quest;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.utils.Debug;

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

	public int getGoal() {
		return goal;
	}

	public String getDescription() {
		return description;
	}

	public int getIndex() {
		return index;
	}

	public int getProgress(PlayerCharacter pc) {
		PlayerQuestManager statusManager = pc.getQuestManager();
		QuestStatus status = statusManager.getQuestStatus(quest);
		if (status == null) {
			Debug.log("null");
			return 0;
		}
		return status.getProgress(this);
	}

	public void setProgress(PlayerCharacter pc, int progress) {
		PlayerQuestManager questManager = pc.getQuestManager();
		QuestStatus status = questManager.getQuestStatus(quest);
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
