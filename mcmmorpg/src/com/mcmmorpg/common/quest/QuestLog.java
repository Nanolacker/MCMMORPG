package com.mcmmorpg.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.item.ItemFactory;

public class QuestLog {

	private static final Map<Quest, ItemStack> questsToInteractables = new HashMap<>();
	private static final Map<ItemStack, Quest> interactablesToQuests = new HashMap<>();

	static {
		List<Quest> quests = Quest.getAll();
		for (Quest quest : quests) {
			ItemStack interactable = ItemFactory.createItemStack(quest.getName(), null, Material.BOOK);
			ItemFactory.registerStaticInteractable(interactable);
			questsToInteractables.put(quest, interactable);
			interactablesToQuests.put(interactable, quest);
		}
		EventManager.registerEvents(new QuestLogListener());
	}

	private static class QuestLogListener implements Listener {
		@EventHandler
		private void onInteract(StaticInteractableEvent event) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			ItemStack interactable = event.getInteractable();
			Quest quest = interactablesToQuests.get(interactable);
			pc.setTargetQuest(quest);
		}
	}

	private final PlayerCharacter pc;

	public QuestLog(PlayerCharacter pc) {
		this.pc = pc;
	}

	private Inventory createInventory() {
		int slots = 3 * 9;
//		Inventory inventory = new Menu("Quest Log", menuSlots);
//		List<Quest> inProgressQuests = getInProgressQuests();
//		for (int i = 0; i < menuSlots && i < inProgressQuests.size(); i++) {
//			Quest quest = inProgressQuests.get(i);
//			ItemStack interactable = questsToInteractables.get(quest);
//			menu.addItem(i, interactable);
//		}
//		return menu;
		return null;
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
//		Menu menu = createMenu();
//		menu.open(pc.getPlayer());
	}

}
