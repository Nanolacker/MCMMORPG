package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterDropItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterLootItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterPickUpItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterReceiveItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterRegisterEvent;
import com.mcmmorpg.common.event.PlayerCharacterRemoveItemEvent;
import com.mcmmorpg.common.event.QuestObjectiveAccessibilityChangeEvent;
import com.mcmmorpg.common.item.Item;

class QuestObjectiveListener implements Listener {

	private static final Map<Item, QuestObjective> ITEMS_TO_ITEM_COLLECTION_OBJECTIVES = new HashMap<>();
	private static final Map<QuestObjective, Item> ITEM_COLLECTION_OBJECTIVES_TO_ITEMS = new HashMap<>();

	static void registerItemCollectionObjective(Item item, QuestObjective objective) {
		ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.put(item, objective);
		ITEM_COLLECTION_OBJECTIVES_TO_ITEMS.put(objective, item);
	}

	@EventHandler
	private void onRegisterPc(PlayerCharacterRegisterEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		Set<Item> items = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.keySet();
		for (Item item : items) {
			QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
			int itemCount = pc.getItemCount(item);
			objective.setProgress(pc, itemCount);
		}
	}

	@EventHandler
	private void onLootItem(PlayerCharacterLootItemEvent event) {
		Item item = event.getItem();
		QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
		if (objective == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		int amount = event.getAmount();
		objective.addProgress(pc, amount);
	}

	@EventHandler
	private void onPickUpItem(PlayerCharacterPickUpItemEvent event) {
		Item item = event.getItem();
		QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
		if (objective == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		int amount = event.getAmount();
		objective.addProgress(pc, amount);
	}

	@EventHandler
	private void onReceiveItem(PlayerCharacterReceiveItemEvent event) {
		Item item = event.getItem();
		QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
		if (objective == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		int amount = event.getAmount();
		objective.addProgress(pc, amount);
	}

	@EventHandler
	private void onDropItem(PlayerCharacterDropItemEvent event) {
		Item item = event.getItem();
		QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
		if (objective == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		int amount = event.getAmount();
		objective.addProgress(pc, -amount);
	}

	@EventHandler
	private void onRemoveItem(PlayerCharacterRemoveItemEvent event) {
		Item item = event.getItem();
		QuestObjective objective = ITEMS_TO_ITEM_COLLECTION_OBJECTIVES.get(item);
		if (objective == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		int amount = event.getAmount();
		objective.addProgress(pc, -amount);
	}

	@EventHandler
	private void onObjectiveChangeAccessibility(QuestObjectiveAccessibilityChangeEvent event) {
		if (!event.objectiveIsAccessible()) {
			return;
		}
		QuestObjective objective = event.getObjective();
		Item item = ITEM_COLLECTION_OBJECTIVES_TO_ITEMS.get(objective);
		if (item == null) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		updateItemCollectionObjectiveProgress(pc, objective, item);
	}

	private void updateItemCollectionObjectiveProgress(PlayerCharacter pc, QuestObjective objective, Item item) {
		int itemCount = pc.getItemCount(item);
		objective.setProgress(pc, itemCount);
	}

}
