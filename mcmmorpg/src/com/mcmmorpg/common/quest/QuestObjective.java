package com.mcmmorpg.common.quest;

import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestObjectiveChangeProgressEvent;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.utils.MathUtils;

import net.md_5.bungee.api.ChatColor;

public class QuestObjective {

	private static final Noise OBJECTIVE_COMPLETE_NOISE = new Noise(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

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
	 * Returns the progress of the player character on this objective.
	 */
	public int getProgress(PlayerCharacter pc) {
		PlayerCharacterQuestManager statusManager = pc.getQuestManager();
		PlayerCharacterQuestData data = statusManager.getQuestData(quest);
		if (data == null) {
			QuestStatus status = quest.getStatus(pc);
			if (status == QuestStatus.COMPLETED) {
				return goal;
			} else if (status == QuestStatus.NOT_STARTED) {
				return 0;
			}
		}
		return data.getProgress(this.index);
	}

	/**
	 * Sets the progress of the player character on this objective.
	 */
	public void setProgress(PlayerCharacter pc, int progress) {
		PlayerCharacterQuestManager questManager = pc.getQuestManager();
		PlayerCharacterQuestData data = questManager.getQuestData(quest);
		if (data == null) {
			// don't do anything
			return;
		}
		int previousProgress = getProgress(pc);
		progress = (int) MathUtils.clamp(progress, 0, goal);
		data.setProgress(this.index, progress);
		QuestObjectiveChangeProgressEvent event = new QuestObjectiveChangeProgressEvent(pc, this, previousProgress,
				progress);
		EventManager.callEvent(event);
		pc.updateQuestDisplay();
		if (progress == goal) {
			pc.sendMessage(description + ChatColor.GRAY + " complete!");
			OBJECTIVE_COMPLETE_NOISE.play(pc);
		}
		quest.checkForCompletion(pc);
	}

	/**
	 * Equivalent to setProgress(getProgress() + progressToAdd). Negative progress
	 * to add will remove progress.
	 */
	public void addProgress(PlayerCharacter pc, int progressToAdd) {
		int progress = getProgress(pc);
		setProgress(pc, progress + progressToAdd);
	}

	public void complete(PlayerCharacter pc) {
		setProgress(pc, goal);
	}

	/**
	 * Returns whether the player character has completed this objective. Returns
	 * true if the player character has finished this quest or false if the player
	 * character has not started this quest.
	 */
	public boolean isComplete(PlayerCharacter pc) {
		return getProgress(pc) == this.goal;
	}

}
