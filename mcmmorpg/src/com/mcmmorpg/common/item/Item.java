package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.util.BukkitUtility;

/**
 * Represents an item that a player character can own. Items take the forms of
 * item stacks in player inventories.
 */
public class Item {

	private static final Map<ItemStack, Item> itemStackMap = new HashMap<>();
	private static final Map<String, Item> itemMap = new HashMap<>();

	private final String name;
	private final ItemRarity rarity;
	private final Material icon;
	private final String description;

	private transient ItemStack itemStack;

	/**
	 * Create a new item. initialize() must be invoked after construction.
	 */
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
		StringBuilder lore = new StringBuilder();
		lore.append(rarity.getColor() + rarity.toString() + " Item");
		if (description != null) {
			lore.append(ChatColor.RESET + "\n\n" + description + "\n\n");
		}
		return BukkitUtility.createItemStack(rarity.getColor() + getName(), lore.toString(), getIcon());

	}

	/**
	 * Returns the item with the specified name.
	 */
	public static Item forName(String name) {
		return itemMap.get(name);
	}

	/**
	 * Returns the item that corresponds to the specified item stack, or null if the
	 * specified item stack does not correspond to an item.
	 */
	public static Item forItemStack(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemStack unitItemStack = itemStack.clone();
		unitItemStack.setAmount(1);
		return itemStackMap.get(unitItemStack);
	}

	/**
	 * Returns the name of this item.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the rarity of this item.
	 */
	public final ItemRarity getRarity() {
		return rarity;
	}

	/**
	 * Returns the material used in item stacks for this item.
	 */
	public final Material getIcon() {
		return icon;
	}

	/**
	 * Returns the description, as seen in item stacks, of this item.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Returns a clone of this item's item stack.
	 */
	public final ItemStack getItemStack() {
		return itemStack.clone();
	}

	/**
	 * Drops 1 count of this item at the specified location.
	 **/
	public void drop(Location location) {
		drop(location, 1);
	}

	/**
	 * Drops item at the specified location in the specified amount.
	 **/
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

	/**
	 * Has a chance to drop this item at the specified location according to the
	 * specified probability.
	 */
	public void drop(Location location, double probability) {
		drop(location, 1, probability);
	}

	/**
	 * Has a chance to drop this item a max number of times at the specified
	 * location according to the specified probability.
	 */
	public void drop(Location location, int maxAmount, double probability) {
		int amount = 0;
		for (int i = 0; i < maxAmount; i++) {
			if (Math.random() < probability) {
				amount++;
			}
		}
		drop(location, amount);
	}

	@Override
	public String toString() {
		return rarity.getColor() + name;
	}

	/**
	 * Returns this items name colored according to its rarity and surrounded with
	 * brackets.
	 */
	public String formatName() {
		return ChatColor.GRAY + "[" + toString() + ChatColor.GRAY + "]";
	}

}
