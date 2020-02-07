package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

	protected void initialize() {
		this.itemStack = createItemStack();
		itemStackMap.put(itemStack, this);
		idMap.put(id, this);
	}

	protected ItemStack createItemStack() {
		String lore = rarity + "\n" + description;
		return ItemFactory.createItemStack(rarity.getColor() + name, lore, icon);
	}

	public static Item forID(int id) {
		return idMap.get(id);
	}

	public static Item forItemStack(ItemStack itemStack) {
		return itemStackMap.get(itemStack);
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
		return itemStack;
	}

}
