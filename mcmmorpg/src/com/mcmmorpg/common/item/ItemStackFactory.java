package com.mcmmorpg.common.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcmmorpg.common.utils.StringUtils;

public class ItemStackFactory {

	private ItemStackFactory() {
	}

	public static ItemStack create(String name, String lore, Material material) {
		List<String> loreAsList = StringUtils.paragraph(lore);
		return create0(name, loreAsList, material);
	}

	public static ItemStack create0(String name, List<String> lore, Material material) {
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

}
