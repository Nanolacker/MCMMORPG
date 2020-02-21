package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class Weapon extends Item {

	private final String playerClass;
	private final int level;

	private transient PlayerClass playerClass0;

	public Weapon(int id, String name, ItemRarity rarity, Material icon, String description, String playerClass,
			int level) {
		super(id, name, rarity, icon, description);
		this.playerClass = playerClass;
		this.level = level;
	}

	@Override
	public void initialize() {
		super.initialize();
		playerClass0 = PlayerClass.forName(playerClass);
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String lore = ChatColor.GOLD + playerClass + " Weapon" + ChatColor.GOLD + "\nLevel " + level + "\n"
				+ rarity.getColor() + rarity + " Item\n\n" + ChatColor.RESET + getDescription();
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	public int getLevel() {
		return level;
	}

}
