package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Item {

	private static final Map<ItemStack, Item> itemStackMap = new HashMap<>();
	private static final Map<String, Item> itemMap = new HashMap<>();

	private final String name;
	private final ItemRarity rarity;
	private final Material icon;
	private final String description;

	private transient ItemStack itemStack;

	public Item(String name, ItemRarity rarity, Material icon, String description) {
		this.name = name;
		this.rarity = rarity;
		this.icon = icon;
		this.description = description;
	}

	public void initialize() {
		this.itemStack = createItemStack();
		itemStackMap.put(itemStack, this);
		itemMap.put(name, this);
	}

	protected ItemStack createItemStack() {
		String lore = rarity.getColor() + rarity.toString() + " Item\n\n"
				+ (description == null ? "" : (ChatColor.RESET + description));
		return ItemFactory.createItemStack(rarity.getColor() + name, lore, icon);
	}

	public static Item forName(String name) {
		return itemMap.get(name);
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

	public void drop(Location location, int amount) {
		if (amount == 0) {
			return;
		}
		World world = location.getWorld();
		ItemStack itemStack = this.itemStack.clone();
		itemStack.setAmount(amount);
		org.bukkit.entity.Item entity = (org.bukkit.entity.Item) world.dropItem(location, itemStack);
		entity.setCustomName(this.toString());
		entity.setCustomNameVisible(true);
	}

	@Override
	public String toString() {
		return rarity.getColor() + name;
	}

	public String formatName() {
		return ChatColor.WHITE + "[" + toString() + ChatColor.WHITE + "]";
	}

}
