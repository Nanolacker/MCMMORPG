package com.mcmmorpg.common.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.utils.StringUtils;

public class ItemFactory {

	static final List<ItemStack> weapons = new ArrayList<>();
	static final List<ItemStack> consumables = new ArrayList<>();

	static {
		EventManager.registerEvents(new ItemListener());
	}

	private ItemFactory() {
	}

	public static ItemStack createItemStack(String name, String lore, Material material) {
		List<String> loreAsList = StringUtils.lineSplit(lore);
		return createItemStack0(name, loreAsList, material);
	}

	public static ItemStack createItemStack0(String name, List<String> lore, Material material) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		if (lore != null) {
			itemMeta.setLore(lore);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static void registerWeapon(ItemStack itemStack) {
		weapons.add(itemStack);
	}

	public static void registerConsumable(ItemStack itemStack) {
		consumables.add(itemStack);
	}

}
