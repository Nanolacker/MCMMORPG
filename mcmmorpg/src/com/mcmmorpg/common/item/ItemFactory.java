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

	 static final List<ItemStack> consumableItems = new ArrayList<>();
	 static final List<ItemStack> weaponItems = new ArrayList<>();

	static {
		EventManager.registerEvents(new ItemListener());
	}

	private ItemFactory() {
	}

	public static ItemStack createItemStack(String name, String lore, Material material) {
		List<String> loreAsList = StringUtils.paragraph(lore);
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

	public ItemStack createConsumable(String name, String description, Material material) {
		ItemStack itemStack = createItemStack(name, description, material);
		consumableItems.add(itemStack);
		return itemStack;
	}

}
