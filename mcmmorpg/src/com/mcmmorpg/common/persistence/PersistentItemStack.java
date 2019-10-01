package com.mcmmorpg.common.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PersistentItemStack {

	private final Material type;
	private final int amount;
	private String displayName;
	private String[] lore;
	private ItemFlag[] flags;

	public PersistentItemStack(ItemStack itemStack) {
		this.type = itemStack.getType();
		this.amount = itemStack.getAmount();
		ItemMeta itemMeta = itemStack.getItemMeta();
		this.displayName = itemMeta.getDisplayName();
		this.lore = (String[]) itemMeta.getLore().toArray();
		this.flags = (ItemFlag[]) itemMeta.getItemFlags().toArray();
	}

	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(type, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(Arrays.asList(lore));
		itemMeta.addItemFlags(flags);
		return itemStack;
	}

}
