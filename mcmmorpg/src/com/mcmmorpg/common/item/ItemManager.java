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

	private static final Map<String, ItemStack> itemMap;
	private static final Map<ItemStack, ItemListener> listenerMap;

	static {
		itemMap = new HashMap<>();
		listenerMap = new HashMap<>();
		EventManager.registerEvents(new ItemManager());
	}

	private ItemManager() {
	}

	public static ItemStack getItemForID(String id) {
		return itemMap.get(id);
	}

	/**
	 * 
	 * @param itemID allows retrieval of item through
	 *               {@link ItemManager#getItemForID(String)}
	 */
	public static void registerItem(String itemID, ItemStack item, ItemListener listener) {
		item = simplifyItem(item);
		itemMap.put(itemID, item);
		listenerMap.put(item, listener);
	}

	private static ItemListener listenerForItemStack(ItemStack item) {
		item = simplifyItem(item);
		return listenerMap.get(item);
	}

	private static ItemStack simplifyItem(ItemStack item) {
		item = item.clone();
		item.setAmount(1);
		return item;
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		ItemListener listener = listenerForItemStack(itemStack);
		if (listener != null) {
			listener.onInventoryClick(event);
		}
	}

	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event) {
		ItemStack itemStack = event.getOldCursor();
		ItemListener listener = listenerForItemStack(itemStack);
		if (listener != null) {
			listener.onInventoryDrag(event);
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemListener listener = listenerForItemStack(itemStack);
		if (listener != null) {
			listener.onInteract(event);
		}
	}

	@EventHandler
	private void onPickup(InventoryPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		ItemListener listener = listenerForItemStack(itemStack);
		if (listener != null) {
			listener.onPickup(event);
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		ItemListener listener = listenerMap.get(itemStack);
		if (listener != null) {
			listener.onDrop(event);
		}
	}

}
