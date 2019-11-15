package com.mcmmorpg.common.item;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

	protected void onClick(InventoryClickEvent event) {
	}

	protected void onDrag(InventoryDragEvent event) {
	}

	protected void onInteract(PlayerInteractEvent event) {
	}

	protected void onPickup(InventoryPickupItemEvent event) {
	}

	protected void onDrop(PlayerDropItemEvent event) {
	}

}
