package com.mcmmorpg.common.persistence;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class PersistentInventory {

	// see if ItemStacks can be stored in JSON
	private final ItemStack[] contents;

	public PersistentInventory(ItemStack[] contents) {
		this.contents = contents;
	}

	public PersistentInventory(Inventory inventory) {
		this(inventory.getContents());
	}

	public ItemStack[] getContents() {
		return contents;
	}

}
