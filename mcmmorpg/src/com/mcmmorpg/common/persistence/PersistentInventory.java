package com.mcmmorpg.common.persistence;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class PersistentInventory {

	// see if ItemStacks can be stored in JSON
	private final PersistentItemStack[] persistentContents;

	public PersistentInventory(ItemStack[] contents) {
		this.persistentContents = new PersistentItemStack[contents.length];
		for (int i = 0; i < contents.length; i++) {
			ItemStack itemStack = contents[i];
			if (itemStack != null) {
				PersistentItemStack persistentItemStack = new PersistentItemStack(itemStack);
				persistentContents[i] = persistentItemStack;
			}
		}
	}

	public PersistentInventory(Inventory inventory) {
		this(inventory.getContents());
	}

	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[persistentContents.length];
		for (int i = 0; i < contents.length; i++) {
			PersistentItemStack persistentItemStack = persistentContents[i];
			if (persistentItemStack != null) {
				ItemStack itemStack = persistentItemStack.getItemStack();
				contents[i] = itemStack;
			}
		}
		return contents;
	}

}
