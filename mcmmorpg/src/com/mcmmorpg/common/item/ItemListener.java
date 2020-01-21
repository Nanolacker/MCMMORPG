package com.mcmmorpg.common.item;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.ConsumableItemEvent;
import com.mcmmorpg.common.event.EventManager;

class ItemListener implements Listener {

	@EventHandler
	private void onRightClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack itemStack = event.getCurrentItem();
		if (ItemFactory.consumableItems.contains(itemStack)) {
			ConsumableItemEvent consumableUseEvent = new ConsumableItemEvent(pc, itemStack);
			EventManager.callEvent(consumableUseEvent);
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
		ItemStack itemStack = item.getItemStack();
		ItemDiscardMenu menu = new ItemDiscardMenu(pc, itemStack);
		menu.open();
	}

	@EventHandler
	private void onDiscardMenuClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack itemStack = event.getCurrentItem();
		handleDiscardEvent(pc, itemStack);
		event.setCancelled(true);
	}

	@EventHandler
	private void onDiscardMenuDrag(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack itemStack = event.getOldCursor();
		handleDiscardEvent(pc, itemStack);
		event.setCancelled(true);
	}

	@EventHandler
	private void onDiscardMenuClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		ItemStack itemStack = player.getItemOnCursor();
		handleDiscardEvent(pc, itemStack);
	}

	private void handleDiscardEvent(PlayerCharacter pc, ItemStack itemStack) {
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		Player player = pc.getPlayer();
		if (itemStack.equals(ItemDiscardMenu.CANCEL_ITEM_STACK)) {
			// allow player to place item somewhere in inventory
			player.openInventory(player.getInventory());
			player.setItemOnCursor(menu.itemStack);
		} else if (itemStack.equals(ItemDiscardMenu.CONFIRM_ITEM_STACK)) {
			player
			menu.close();
		}
	}

}
