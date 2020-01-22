package com.mcmmorpg.common.item;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.utils.Debug;

class ItemListener implements Listener {

	@EventHandler
	private void onRightClickItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack itemStack = event.getCurrentItem();
		if (ItemFactory.consumables.contains(itemStack)) {
			PlayerCharacterUseConsumableEvent consumableUseEvent = new PlayerCharacterUseConsumableEvent(pc, itemStack);
			EventManager.callEvent(consumableUseEvent);
		}
	}

	@EventHandler
	private void onInteractWithItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		// don't let people place blocks and what not
		event.setCancelled(true);
		ItemStack itemStack = event.getItem();
		if (ItemFactory.weapons.contains(itemStack)) {
			handleWeaponInteraction(pc, itemStack, event.getAction());
		} else if (ItemFactory.consumables.contains(itemStack)) {
			handleConsumableInteraction(pc, itemStack, event.getAction());
		}
	}

	private void handleWeaponInteraction(PlayerCharacter pc, ItemStack weapon, Action action) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			PlayerCharacterUseWeaponEvent event = new PlayerCharacterUseWeaponEvent(pc, weapon);
			EventManager.callEvent(event);
		}
	}

	private void handleConsumableInteraction(PlayerCharacter pc, ItemStack consumable, Action action) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			PlayerCharacterUseConsumableEvent event = new PlayerCharacterUseConsumableEvent(pc, consumable);
			EventManager.callEvent(event);
		}
	}

	@EventHandler
	private void onThrowItem(PlayerDropItemEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Item item = event.getItemDrop();
		item.remove();
	}

}
