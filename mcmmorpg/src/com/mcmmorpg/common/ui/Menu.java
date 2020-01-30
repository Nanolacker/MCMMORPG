package com.mcmmorpg.common.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.item.ItemFactory;

public class Menu {

	private final Inventory inventory;

	public Menu(String title, int rowCount) {
		inventory = Bukkit.createInventory(null, 9 * rowCount, title);
	}

	/**
	 * Adds an interactable item stack to this menu. Ensure that the item stack has
	 * been registered as a static interactable prior or call
	 * registerInteractables().
	 */
	public void addInteractable(int slot, ItemStack staticInteractable) {
		inventory.setItem(slot, staticInteractable);
	}

	public void open(Player player) {
		player.openInventory(inventory);
	}

	public void registerInteractables() {
		ItemStack[] contents = inventory.getContents();
		for (ItemStack itemStack : contents) {
			ItemFactory.registerStaticInteractable(itemStack);
		}
	}

	public void unregisterInteractables() {
		ItemStack[] contents = inventory.getContents();
		for (ItemStack itemStack : contents) {
			ItemFactory.unregisterStaticInteractable(itemStack);
		}
	}

}
