package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Item {

	private static final Map<ItemStack, Item> itemStackMap = new HashMap<>();
	private static final Map<Integer, Item> idMap = new HashMap<>();

	private final int id;
	private final String name;
	private final ItemRarity rarity;
	private final Material icon;
	private final String description;

	private transient ItemStack itemStack;

	public Item(int id, String name, ItemRarity rarity, Material icon, String description) {
		this.id = id;
		this.name = name;
		this.rarity = rarity;
		this.icon = icon;
		this.description = description;
	}

	public void initialize() {
		this.itemStack = createItemStack();
		itemStackMap.put(itemStack, this);
		idMap.put(id, this);
	}

	protected ItemStack createItemStack() {
		String lore = rarity.getColor() + rarity.toString() + " Item\n\n" + ChatColor.RESET + description;
		return ItemFactory.createItemStack(rarity.getColor() + name, lore, icon);
	}

	public static Item forID(int id) {
		return idMap.get(id);
	}

	/**
	 * Returns null if the specified item stack does not correspond to an item.
	 */
	public static Item forItemStack(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemStack unitItemStack = itemStack.clone();
		unitItemStack.setAmount(1);
		return itemStackMap.get(unitItemStack);
	}

	public final int getID() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final ItemRarity getRarity() {
		return rarity;
	}

	public final Material getIcon() {
		return icon;
	}

	public final String getDescription() {
		return description;
	}

	public final ItemStack getItemStack() {
		return itemStack.clone();
	}

	@Override
	public String toString() {
		return rarity.getColor() + name;
	}

}
