package com.mcmmorpg.common.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A representation of an item stack that can be serialized.
 */
public final class PersistentItemStack {

	private final Material type;
	private final int amount;
	private final String displayName;
	private final String[] lore;
	private final ItemFlag[] flags;
	private final boolean unbreakable;

	/**
	 * Creates a persistent item stack from the specified item stack.
	 */
	public PersistentItemStack(ItemStack itemStack) {
		this.type = itemStack.getType();
		this.amount = itemStack.getAmount();
		ItemMeta itemMeta = itemStack.getItemMeta();
		this.displayName = itemMeta.getDisplayName();
		List<String> lore = itemMeta.getLore();
		if (lore == null) {
			this.lore = null;
		} else {
			this.lore = lore.toArray(new String[lore.size()]);
		}
		Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
		this.flags = itemFlags.toArray(new ItemFlag[itemFlags.size()]);
		this.unbreakable = itemMeta.isUnbreakable();
	}

	/**
	 * Converts this persistent item stack to an item stack.
	 */
	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(type, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (lore != null) {
			itemMeta.setLore(Arrays.asList(lore));
		}
		itemMeta.addItemFlags(flags);
		itemMeta.setUnbreakable(unbreakable);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

}
