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
		String lore = ChatColor.GOLD + "Level " + level + "\n" + rarity.getColor() + rarity + " Item\n\n"
				+ description == null ? ""
						: (ChatColor.RESET + description) + ChatColor.GRAY + "\n\nShift-click to use";
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public int getLevel() {
		return level;
	}

}
