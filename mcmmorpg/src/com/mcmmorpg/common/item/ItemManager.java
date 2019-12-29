package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.utils.EventManager;

public class ItemManager implements Listener {

	private static final Map<ItemStack, ItemListener> itemMap = new HashMap<>();

	static {
		EventManager.registerEvents(new ItemManager());
	}

	private ItemManager() {
	}

	public static void registerItemListener(ItemStack itemStack, ItemListener listener) {
		itemMap.put(itemStack, listener);
	}

	@EventHandler
	private void onClick(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		ItemListener listener = itemMap.get(itemStack);
		if (listener != null) {
			listener.onInventoryClick(event);
		}
	}

	@EventHandler
	private void onDrag(InventoryDragEvent event) {
		ItemStack itemStack = event.getOldCursor();
		ItemListener listener = itemMap.get(itemStack);
		if (listener != null) {
			listener.onInventoryDrag(event);
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemListener listener = itemMap.get(itemStack);
		if (listener != null) {
			listener.onInteract(event);
		}
	}

	@EventHandler
	private void onPickup(InventoryPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		ItemListener listener = itemMap.get(itemStack);
		if (listener != null) {
			listener.onPickup(event);
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		ItemListener listener = itemMap.get(itemStack);
		if (listener != null) {
			listener.onDrop(event);
		}
	}

}
