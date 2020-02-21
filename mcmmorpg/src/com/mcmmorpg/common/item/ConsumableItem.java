package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ConsumableItem extends Item {

	private final int level;

	public ConsumableItem(int id, String name, ItemRarity rarity, Material icon, String description, int level) {
		super(id, name, rarity, icon, description);
		this.level = level;
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String lore = ChatColor.GOLD + "Level " + level + "\n" + rarity.getColor() + rarity + " Item\n\n"
				+ ChatColor.RESET + getDescription() + ChatColor.GRAY + "\n\nShift-click to use";
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public int getLevel() {
		return level;
	}

}
