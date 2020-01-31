package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.item.ItemFactory;

public class QuestLog {

	private static final Map<Quest, ItemStack> interactables = new HashMap<>();

	static {
		for (Quest quest : quests) {

		}
		EventManager.registerEvents(new QuestLogListener());
	}

	private static class QuestLogListener implements Listener {
		@EventHandler
		private void onInteract(StaticInteractableEvent event) {
			ItemStack interactable = event.getInteractable();

		}
	}

	private final PlayerCharacter pc;
	private final Inventory inventory;

	public QuestLog(PlayerCharacter pc) {
		this.pc = pc;
		inventory = createInventory();
	}

	private Inventory createInventory() {
		int questCount = 3 * 9;
		Inventory inventory = Bukkit.createInventory(null, questCount, "Quest Log");
		PlayerQuestManager manager = pc.getQuestManager();
		Quest[] inProgressQuests = manager.getInProgressQuests();
		for (int i = 0; i < questCount && i < inProgressQuests.length; i++) {
			Quest quest = inProgressQuests[i];
			ItemStack itemStack = getItemStack(quest);
		}
		return inventory;
	}

	private ItemStack getItemStack(Quest quest) {
		ItemStack itemStack = ItemFactory.createItemStack(quest.getName(), null, Material.PAPER);
		ItemFactory.registerStaticInteractable(itemStack);
	}

	public void open() {
		pc.getPlayer().openInventory(inventory);
	}

}
