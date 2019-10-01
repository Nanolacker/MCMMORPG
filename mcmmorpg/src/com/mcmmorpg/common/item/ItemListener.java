package com.mcmmorpg.common.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

	@EventHandler
	protected void onClick(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		GameItem gameItem = GameItem.forItemStack(itemStack);
		if (gameItem != null) {
			gameItem.onClick(event);
		}
	}

	@EventHandler
	protected void onDrag(InventoryDragEvent event) {
		ItemStack itemStack = event.getOldCursor();
		GameItem gameItem = GameItem.forItemStack(itemStack);
		if (gameItem != null) {
			gameItem.onDrag(event);
		}
	}

	@EventHandler
	protected void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		GameItem gameItem = GameItem.forItemStack(itemStack);
		if (gameItem != null) {
			gameItem.onInteract(event);
		}
	}
	
	@EventHandler
	protected void onPickup(InventoryPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		GameItem gameItem = GameItem.forItemStack(itemStack);
		if (gameItem != null) {
			gameItem.onPickup(event);
		}
	}

	@EventHandler
	protected void onDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		GameItem gameItem = GameItem.forItemStack(itemStack);
		if (gameItem != null) {
			gameItem.onDrop(event);
		}
	}

}
