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

public class QuestLog {

	private static final List<Inventory> questLogInventories = new ArrayList<>();

	static {
		EventManager.registerEvents(new QuestLogListener());
	}

	private final PlayerCharacter pc;

	public QuestLog(PlayerCharacter pc) {
		this.pc = pc;
	}

	private Inventory createInventory() {
		int size = 9 * 1;
		Inventory inventory = Bukkit.createInventory(null, size, "Quest Log");
		List<Quest> inProgressQuests = Quest.getInProgressQuests(pc);
		for (int i = 0; i < size && i < inProgressQuests.size(); i++) {
			Quest quest = inProgressQuests.get(i);
			ItemStack itemStack = quest.getQuestLogItemStack(pc);
			inventory.setItem(i, itemStack);
		}
		return inventory;
	}

	public void open() {
		Inventory inventory = createInventory();
		pc.getPlayer().openInventory(inventory);
		questLogInventories.add(inventory);
	}

	private static class QuestLogListener implements Listener {
		@EventHandler
		private void onClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			Inventory topInventory = event.getInventory();
			if (questLogInventories.contains(topInventory)) {
				event.setCancelled(true);
				Inventory clickedInventory = event.getClickedInventory();
				if (clickedInventory == topInventory) {
					// Clicking an empty slot resets the quest tracking.
					ItemStack itemStack = event.getCurrentItem();
					if (itemStack == null) {
						pc.setTargetQuest(null);
					} else {
						String questName = itemStack.getItemMeta().getDisplayName().substring(2);
						Quest quest = Quest.forName(questName);
						pc.setTargetQuest(quest);
					}
				}
			}
		}

		@EventHandler
		private void onClose(InventoryCloseEvent event) {
			questLogInventories.remove(event.getInventory());
		}
	}

}
