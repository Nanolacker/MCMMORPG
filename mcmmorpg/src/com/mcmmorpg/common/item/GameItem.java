package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.MMORPGPlugin;

public class GameItem {

	private static final Map<ItemStack, GameItem> itemMap = new HashMap<>();

	private ItemStack itemStack;

	static {
		MMORPGPlugin.registerEvents(new ItemListener());
	}

	public GameItem(ItemStack itemStack) {
		itemStack.setAmount(1);
		this.itemStack = itemStack;
		itemMap.put(itemStack, this);
	}

	static GameItem forItemStack(ItemStack itemStack) {
		return itemMap.get(itemStack);
	}

	public ItemStack getItemStack() {
		return getItemStack(1);
	}

	public ItemStack getItemStack(int amount) {
		ItemStack itemStack = this.itemStack.clone();
		itemStack.setAmount(amount);
		return itemStack;
	}

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
