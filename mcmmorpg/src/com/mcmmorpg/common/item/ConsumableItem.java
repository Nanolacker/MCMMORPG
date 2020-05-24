package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ConsumableItem extends Item {

	private final int level;

	public ConsumableItem(String name, ItemRarity rarity, Material icon, String description, int level) {
		super(name, rarity, icon, description);
		this.level = level;
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String description = getDescription();
		StringBuilder lore = new StringBuilder();
		lore.append(rarity.getColor() + rarity.toString() + " Item\n");
		lore.append(ChatColor.GOLD + "Level " + level + "\n");
		if (description != null) {
			lore.append(ChatColor.RESET + description + "\n\n");
		}
		lore.append(ChatColor.GRAY + "Shift-click to use");
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore.toString(), getIcon());

	}

	public int getLevel() {
		return level;
	}

}
