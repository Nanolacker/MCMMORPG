package com.mcmmorpg.common.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.util.StringUtility;

/**
 * Provides means to easily create item stacks and register static interactables
 * (i.e. "button" item stacks).
 */
public class ItemFactory {

	static final List<ItemStack> staticInteractables = new ArrayList<>();

	static {
		EventManager.registerEvents(new ItemListener());
	}

	private ItemFactory() {
	}

	/**
	 * Convenience method for creating item stacks.
	 */
	public static ItemStack createItemStack(String name, String lore, Material material) {
		List<String> loreAsList = StringUtility.lineSplit(lore);
		return createItemStack0(name, loreAsList, material);
	}

	/**
	 * Convenience method for creating item stacks.
	 */
	public static ItemStack createItemStack0(String name, List<String> lore, Material material) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		if (lore != null) {
			itemMeta.setLore(lore);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	/**
	 * Register an immovable item which calls a StaticInteractableEvent when
	 * interacted with a right click or when clicked on with a right click.
	 */
	public static void registerStaticInteractable(ItemStack itemStack) {
		staticInteractables.add(itemStack);
	}

	/**
	 * Unregister a static interactable item stack.
	 */
	public static void unregisterStaticInteractable(ItemStack itemStack) {
		staticInteractables.remove(itemStack);
	}

}
