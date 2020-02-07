package com.mcmmorpg.common.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class ConsumableItem extends Item {

	private final int level;

	public ConsumableItem(int id, String name, PlayerClass playerClass, ItemRarity rarity, double protections,
			Material icon, String description, int level) {
		super(id, name, rarity, icon, description);
		this.level = level;
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String lore = rarity + " Consumable\n" + getDescription();
		return ItemFactory.createItemStack(rarity + getName(), lore, getIcon());
	}

	public int getLevel() {
		return level;
	}

}
