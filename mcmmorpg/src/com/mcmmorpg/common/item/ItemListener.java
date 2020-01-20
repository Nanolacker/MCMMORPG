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

	private void onDiscardCancelClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		handleDiscardCancelEvent(menu);
	}

	private void onDiscardCancelDrag(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		handleDiscardCancelEvent(menu);
	}

	private void onDiscardCloseMenu(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		handleDiscardCancelEvent(menu);
	}

	private void handleDiscardCancelEvent(ItemDiscardMenu menu) {
		Player player = menu.pc.getPlayer();
		player.openInventory(player.getInventory());
		player.setItemOnCursor(menu.itemStack);
	}

	private void onDiscardConfirmClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		handleDiscardConfirmEvent(menu);
	}

	private void onDiscardConfirmDrag(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		ItemDiscardMenu menu = ItemDiscardMenu.forPC(pc);
		if (menu == null) {
			return;
		}
		handleDiscardConfirmEvent(menu);
	}

	private void handleDiscardConfirmEvent(ItemDiscardMenu menu) {
		Player player = menu.pc.getPlayer();
		player.setItemOnCursor(null);
		player.closeInventory();
	}

}
