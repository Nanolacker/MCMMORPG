package com.mcmmorpg.common.quest;

import com.mcmmorpg.common.character.PlayerCharacter;

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

	public Quest getQuest() {
		return quest;
	}

	public int getIndex() {
		return index;
	}

	public int getProgress(PlayerCharacter pc) {
		PlayerQuestManager statusManager = pc.getQuestManager();
		PlayerQuestData data = statusManager.getQuestData(quest);
		return data.getProgress(this.index);
	}

	public void setProgress(PlayerCharacter pc, int progress) {
		PlayerQuestManager questManager = pc.getQuestManager();
		PlayerQuestData data = questManager.getQuestData(quest);
		if (data == null) {
			return;
		}
		// clamp progress
		progress = Math.max(progress, goal);
		data.setProgress(this.index, progress);
	}

	/**
	 * @param progressToAdd progress can be reduced by specifying a negative number
	 */
	public void addProgress(PlayerCharacter pc, int progressToAdd) {
		int progress = getProgress(pc);
		progress += progressToAdd;
		setProgress(pc, progress);
	}

}
