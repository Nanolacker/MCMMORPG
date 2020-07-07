package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterCloseQuestLogEvent;
import com.mcmmorpg.common.event.PlayerCharacterOpenQuestLogEvent;
import com.mcmmorpg.common.ui.SidebarText;
import com.mcmmorpg.common.util.BukkitUtility;

/**
 * Displays quest status to a player character.
 */
public class QuestLog {

	private static final List<Inventory> questLogInventories = new ArrayList<>();

	static {
		EventManager.registerEvents(new QuestLogListener());
	}

	private final PlayerCharacter pc;

	/**
	 * Create a quest log for the specified player character.
	 */
	public QuestLog(PlayerCharacter pc) {
		this.pc = pc;
	}

	/**
	 * Opens the quest log for the player character.
	 */
	public void open() {
		Inventory inventory = createQuestLogInventory();
		pc.getPlayer().openInventory(inventory);
		questLogInventories.add(inventory);
		PlayerCharacterOpenQuestLogEvent openQuestLogEvent = new PlayerCharacterOpenQuestLogEvent(pc);
		EventManager.callEvent(openQuestLogEvent);
	}

	private Inventory createQuestLogInventory() {
		int size = 9 * 3;
		Inventory inventory = Bukkit.createInventory(null, size, "Quest Log");
		List<Quest> inProgressQuests = Quest.getAllQuestsMatchingStatus(pc, QuestStatus.IN_PROGRESS);
		for (int i = 0; i < size && i < inProgressQuests.size(); i++) {
			Quest quest = inProgressQuests.get(i);
			ItemStack itemStack = getInProgressQuestLogItemStack(pc, quest, i);
			inventory.setItem(i, itemStack);
		}
		List<Quest> completedQuests = Quest.getAllQuestsMatchingStatus(pc, QuestStatus.COMPLETED);
		for (int i = 0; i < size && i < completedQuests.size(); i++) {
			Quest quest = completedQuests.get(i);
			ItemStack itemStack = getCompletedQuestLogItemStack(pc, quest);
			inventory.setItem(inProgressQuests.size() + i, itemStack);
		}
		return inventory;
	}

	public void updateSidebarText() {
		String lines = "";
		List<Quest> currentQuests = Quest.getAllQuestsMatchingStatus(pc, QuestStatus.IN_PROGRESS);
		for (int i = 0; i < currentQuests.size(); i++) {
			Quest quest = currentQuests.get(i);
			int questNum = i + 1;
			String objectiveLines = "";
			for (QuestObjective objective : quest.getObjectives()) {
				if (!objective.isAccessible(pc) || objective.isComplete(pc)) {
					continue;
				}
				int progress = objective.getProgress(pc);
				int goal = objective.getGoal();
				ChatColor chatColor = progress == goal ? ChatColor.GREEN : ChatColor.WHITE;
				String progressText = chatColor + "" + ChatColor.BOLD + "- " + progress + "/" + goal;
				objectiveLines += progressText + " " + ChatColor.RESET + objective.getDescription() + "\n";
			}
			lines += ChatColor.YELLOW + "" + "(" + ChatColor.BOLD + questNum + ChatColor.RESET + "" + ChatColor.YELLOW
					+ ") " + quest.getName() + ChatColor.RESET + "\n" + objectiveLines + "\n";
		}
		SidebarText questDisplay = new SidebarText(ChatColor.YELLOW + "Quests", lines);
		questDisplay.apply(pc);
	}

	private ItemStack getInProgressQuestLogItemStack(PlayerCharacter pc, Quest quest, int questIndex) {
		int questNum = questIndex + 1;
		String name = ChatColor.YELLOW + "(" + questNum + ") " + quest.getName();
		String objectiveLines = "";
		for (QuestObjective objective : quest.getObjectives()) {
			if (!objective.isAccessible(pc)) {
				continue;
			}
			int progress = objective.getProgress(pc);
			int goal = objective.getGoal();
			ChatColor chatColor = progress == goal ? ChatColor.GREEN : ChatColor.WHITE;
			String progressText = chatColor + "" + ChatColor.BOLD + "- " + progress + "/" + goal;
			objectiveLines += progressText + " " + ChatColor.RESET + objective.getDescription() + "\n";
		}
		String lore = ChatColor.GOLD + "Level " + quest.getLevel() + " Quest\n" + ChatColor.WHITE + objectiveLines;
		return BukkitUtility.createItemStack(ChatColor.YELLOW + name, lore, Material.BOOK);
	}

	private ItemStack getCompletedQuestLogItemStack(PlayerCharacter pc, Quest quest) {
		String name = ChatColor.GREEN + quest.getName();
		String objectiveLines = "";
		for (QuestObjective objective : quest.getObjectives()) {
			int progress = objective.getProgress(pc);
			int goal = objective.getGoal();
			String progressText = ChatColor.GREEN + "" + ChatColor.BOLD + "- " + progress + "/" + goal;
			objectiveLines += progressText + " " + ChatColor.RESET + objective.getDescription() + "\n";
		}
		String lore = ChatColor.GOLD + "Level " + quest.getLevel() + " Quest\n" + objectiveLines;
		return BukkitUtility.createItemStack(ChatColor.YELLOW + name, lore, Material.BOOK);
	}

	private static class QuestLogListener implements Listener {
		@EventHandler
		private void onClick(InventoryClickEvent event) {
			Inventory topInventory = event.getInventory();
			if (questLogInventories.contains(topInventory)) {
				event.setCancelled(true);
			}
		}

		@EventHandler
		private void onClose(InventoryCloseEvent event) {
			Player player = (Player) event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			Inventory inventory = event.getInventory();
			if (questLogInventories.contains(inventory)) {
				questLogInventories.remove(inventory);
				PlayerCharacterCloseQuestLogEvent closeQuestLogEvent = new PlayerCharacterCloseQuestLogEvent(pc);
				EventManager.callEvent(closeQuestLogEvent);
			}
		}
	}

}
