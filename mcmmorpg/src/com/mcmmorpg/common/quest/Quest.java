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

public class Quest {

	private static final Noise COMPLETE_NOISE = new Noise(Sound.ENTITY_PLAYER_LEVELUP);

	private static final Map<String, Quest> quests;

	static {
		quests = new HashMap<>();
	}

	private final String name;
	private final int level;
	private final QuestObjective[] objectives;
	private final Material icon;

	public Quest(String name, int level, QuestObjective[] objectives, Material icon) {
		this.name = name;
		this.level = level;
		this.objectives = objectives;
		this.icon = icon;
	}

	public void initialize() {
		for (int i = 0; i < objectives.length; i++) {
			QuestObjective objective = objectives[i];
			objective.initialize(this, i);
		}
		quests.put(name, this);
	}

	public static Quest forName(String name) {
		return quests.get(name);
	}

	public static List<Quest> getAll() {
		return new ArrayList<Quest>(quests.values());
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public QuestObjective[] getObjectives() {
		return objectives;
	}

	public QuestObjective getObjective(int index) {
		return objectives[index];
	}

	public Material getIcon() {
		return icon;
	}

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

	public void start(PlayerCharacter pc) {
		if (getStatus(pc) != QuestStatus.NOT_STARTED) {
			return;
		}
		PlayerQuestManager questManager = pc.getQuestManager();
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
		PlayerQuestManager questManager = pc.getQuestManager();
		questManager.completeQuest(this);
		QuestCompletionEvent event = new QuestCompletionEvent(pc, this);
		EventManager.callEvent(event);
		pc.updateQuestDisplay();
	}

	ItemStack getQuestLogItemStack(PlayerCharacter pc) {
		String lore = ChatColor.GOLD + "Level " + level + " Quest\n\n" + getQuestLogLines(pc);
		lore += ChatColor.GRAY + "Click to track this quest";
		return ItemFactory.createItemStack(ChatColor.YELLOW + name, lore, icon);
	}

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
			progressText += "-" + progress + "/" + goal;
			objectiveLines += progressText + " " + ChatColor.WHITE + objective.getDescription() + "\n";
		}
		return objectiveLines;
	}

	public static List<Quest> getInProgressQuests(PlayerCharacter pc) {
		List<Quest> inProgressQuests = new ArrayList<>();
		List<Quest> allQuests = Quest.getAll();
		for (Quest quest : allQuests) {
			if (quest.getStatus(pc) == QuestStatus.IN_PROGRESS) {
				inProgressQuests.add(quest);
			}
		}
		return inProgressQuests;
	}

}
