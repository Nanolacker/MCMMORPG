package com.mcmmorpg.common.quest;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestObjectiveChangeProgressEvent;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.util.MathUtility;

/**
 * Represents a single task for a player character to complete. These are the
 * building blocks of quests.
 */
public class QuestObjective {

	private static final Noise OBJECTIVE_COMPLETE_NOISE = new Noise(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

	private final int goal;
	private final String description;
	private transient Quest quest;
	private transient int index;

	/**
	 * Creates a new quest objective.
	 */
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
		if (data.isAccessible(index)) {
			return data.getProgress(this.index);
		} else {
			return 0;
		}
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
		if (!data.isAccessible(index)) {
			return;
		}
		int previousProgress = getProgress(pc);
		progress = (int) MathUtility.clamp(progress, 0, goal);
		if (progress == previousProgress) {
			return;
		}
		data.setProgress(this.index, progress);
		QuestObjectiveChangeProgressEvent event = new QuestObjectiveChangeProgressEvent(pc, this, previousProgress,
				progress);
		EventManager.callEvent(event);
		pc.updateQuestDisplay();
		if (progress == goal) {
			pc.sendMessage(goal + "/" + goal + " " + description + ChatColor.GRAY + " complete!");
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

	public boolean isAccessible(PlayerCharacter pc) {
		PlayerCharacterQuestManager questManager = pc.getQuestManager();
		PlayerCharacterQuestData data = questManager.getQuestData(quest);
		if (data == null) {
			return false;
		}
		return data.isAccessible(index);
	}

	public void setAccessible(PlayerCharacter pc, boolean accessible) {
		PlayerCharacterQuestManager questManager = pc.getQuestManager();
		PlayerCharacterQuestData data = questManager.getQuestData(quest);
		if (data == null) {
			// don't do anything
			return;
		}
		boolean wasAccessible = data.isAccessible(index);
		if (wasAccessible != accessible) {
			data.setAccessible(index, accessible);
			pc.updateQuestDisplay();
		}
	}

	public void registerAsItemCollectionObjective(Item item) {
		QuestObjectiveListener.registerItemCollectionObjective(item, this);
	}

	public void registerAsSlayCharacterQuest(Class<? extends AbstractCharacter> characterType) {
		QuestObjectiveListener.registerSlayCharacterObjective(characterType, this);
	}

}
