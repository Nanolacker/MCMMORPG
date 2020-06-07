package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.QuestCompletionEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.sound.Noise;

/**
 * Represents a sequence of tasks to be completed by a player character. Usually
 * created with JSON.
 */
public class Quest {

	private static final Noise COMPLETE_NOISE = new Noise(Sound.ENTITY_PLAYER_LEVELUP);

	private static final Map<String, Quest> quests;

	static {
		quests = new HashMap<>();
	}

	private final String name;
	private final int level;
	private final QuestObjective[] objectives;

	/**
	 * Create a new quest.
	 */
	public Quest(String name, int level, QuestObjective[] objectives) {
		this.name = name;
		this.level = level;
		this.objectives = objectives;
	}

	/**
	 * Sets up this quest. This must be called after construction!
	 */
	public void initialize() {
		for (int i = 0; i < objectives.length; i++) {
			QuestObjective objective = objectives[i];
			objective.initialize(this, i);
		}
		quests.put(name, this);
	}

	/**
	 * Returns the quest of the specified name.
	 */
	public static Quest forName(String name) {
		return quests.get(name);
	}

	/**
	 * Returns an array containing every quest in the game.
	 */
	public static Quest[] getAll() {
		return quests.values().toArray(new Quest[quests.size()]);
	}

	/**
	 * Returns the name of this quest.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the recommended level for this quest.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns this quest's objectives.
	 */
	public QuestObjective[] getObjectives() {
		return objectives;
	}

	/**
	 * Returns the quest objective of the specified index.
	 */
	public QuestObjective getObjective(int index) {
		return objectives[index];
	}

	/**
	 * Returns a player character's status on this quest.
	 */
	public QuestStatus getStatus(PlayerCharacter pc) {
		return pc.getQuestManager().getStatus(this);
	}

	/**
	 * Returns true if the player character's status on this quest is equal to the
	 * one specified.
	 */
	public boolean compareStatus(PlayerCharacter pc, QuestStatus status) {
		return getStatus(pc) == status;
	}

	/**
	 * Begin this quest for the specified player character.
	 */
	public void start(PlayerCharacter pc) {
		if (getStatus(pc) != QuestStatus.NOT_STARTED) {
			return;
		}
		PlayerCharacterQuestManager questManager = pc.getQuestManager();
		questManager.startQuest(this);
		pc.sendMessage(ChatColor.GRAY + "Quest started: " + ChatColor.YELLOW + name);
		pc.updateQuestDisplay();
	}

	void checkForCompletion(PlayerCharacter pc) {
		for (QuestObjective objective : objectives) {
			if (!objective.isComplete(pc)) {
				return;
			}
		}
		complete(pc);
	}

	private void complete(PlayerCharacter pc) {
		pc.sendMessage(ChatColor.YELLOW + name + ChatColor.GRAY + " complete!");
		COMPLETE_NOISE.play(pc);
		PlayerCharacterQuestManager questManager = pc.getQuestManager();
		questManager.completeQuest(this);
		QuestCompletionEvent event = new QuestCompletionEvent(pc, this);
		EventManager.callEvent(event);
		pc.updateQuestDisplay();
	}

	ItemStack getQuestLogItemStack(PlayerCharacter pc) {
		String lore = ChatColor.GOLD + "Level " + level + " Quest\n\n" + getQuestLogLines(pc);
		return ItemFactory.createItemStack(ChatColor.YELLOW + name, lore, Material.BOOK);
	}

	/**
	 * Return the lines that should be used to display this quest in the quest log
	 * or quest sidebar.
	 */
	public String getQuestLogLines(PlayerCharacter pc) {
		String objectiveLines = "";
		for (QuestObjective objective : objectives) {
			int progress = objective.getProgress(pc);
			int goal = objective.getGoal();
			String progressText;
			if (progress < goal) {
				progressText = ChatColor.YELLOW + "";
			} else {
				progressText = ChatColor.GREEN + "";
			}
			progressText += "- " + progress + "/" + goal;
			objectiveLines += progressText + " " + ChatColor.WHITE + objective.getDescription() + "\n";
		}
		return objectiveLines;
	}

	/**
	 * Returns a list containing every quest that is in progress for the player
	 * character.
	 */
	public static List<Quest> getInProgressQuests(PlayerCharacter pc) {
		List<Quest> inProgressQuests = new ArrayList<>();
		Quest[] allQuests = Quest.getAll();
		for (Quest quest : allQuests) {
			if (quest.getStatus(pc) == QuestStatus.IN_PROGRESS) {
				inProgressQuests.add(quest);
			}
		}
		return inProgressQuests;
	}

}
