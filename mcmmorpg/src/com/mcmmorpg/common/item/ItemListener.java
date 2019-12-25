package com.mcmmorpg.common.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ItemListener {

	void onInventoryClick(InventoryClickEvent event);

	void onInventoryDrag(InventoryDragEvent event);

	void onInteract(PlayerInteractEvent event);

	void onPickup(InventoryPickupItemEvent event);

	void onDrop(PlayerDropItemEvent event);
}
