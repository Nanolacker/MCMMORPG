package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
import com.mcmmorpg.common.item.ItemFactory;

public class QuestLog {

	private static final Map<Quest, ItemStack> questsToItemStacks = new HashMap<>();
	private static final Map<ItemStack, Quest> itemStacksToQuests = new HashMap<>();
	private static final Map<PlayerCharacter, Inventory> inventoryMap = new HashMap<>();

	static {
		List<Quest> quests = Quest.getAll();
		for (Quest quest : quests) {
			ItemStack interactable = ItemFactory.createItemStack(quest.getName(), null, Material.BOOK);
			ItemFactory.registerStaticInteractable(interactable);
			questsToItemStacks.put(quest, interactable);
			itemStacksToQuests.put(interactable, quest);
		}
		EventManager.registerEvents(new QuestLogListener());
	}

	private final PlayerCharacter pc;

	public QuestLog(PlayerCharacter pc) {
		this.pc = pc;
	}

	private Inventory createInventory() {
		int size = 27;
		Inventory inventory = Bukkit.createInventory(null, size, "Quest Log");
		List<Quest> inProgressQuests = getInProgressQuests();
		for (int i = 0; i < size && i < inProgressQuests.size(); i++) {
			Quest quest = inProgressQuests.get(i);
			ItemStack itemStack = questsToItemStacks.get(quest);
			inventory.setItem(i, itemStack);
		}
		return inventory;
	}

	private List<Quest> getInProgressQuests() {
		List<Quest> inProgressQuests = new ArrayList<>();
		List<Quest> allQuests = Quest.getAll();
		for (Quest quest : allQuests) {
			if (quest.getStatus(pc) == QuestStatus.IN_PROGRESS) {
				inProgressQuests.add(quest);
			}
		}
		return inProgressQuests;
	}

	public void open() {
		Inventory inventory = createInventory();
		pc.getPlayer().openInventory(inventory);
		inventoryMap.put(pc, inventory);
	}

	private static class QuestLogListener implements Listener {
		@EventHandler
		private void onClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			ItemStack itemStack = event.getCurrentItem();
			Quest quest = itemStacksToQuests.get(itemStack);
			// Clicking an empty slot resets the quest tracking.
			pc.setTargetQuest(quest);
		}

		@EventHandler
		private void onClose(InventoryCloseEvent event) {
			Player player = (Player) event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			if (inventoryMap.get(pc) == event.getInventory()) {
				inventoryMap.remove(pc);
			}
		}
	}

}
