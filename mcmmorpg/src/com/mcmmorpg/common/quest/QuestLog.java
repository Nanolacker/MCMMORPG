package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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

/**
 * A menu in which a player character can view quests.
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
			ItemStack itemStack = quest.getInProgressQuestLogItemStack(i, pc);
			inventory.setItem(i, itemStack);
		}
		List<Quest> completedQuests = Quest.getAllQuestsMatchingStatus(pc, QuestStatus.COMPLETED);
		for (int i = 0; i < size && i < completedQuests.size(); i++) {
			Quest quest = completedQuests.get(i);
			ItemStack itemStack = quest.getCompletedQuestLogItemStack(pc);
			inventory.setItem(inProgressQuests.size() + i, itemStack);
		}
		return inventory;
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
