package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

/**
 * An item to be wielded in the main hand of player characters.
 */
public class Weapon extends Item {

	private final String playerClass;
	private final int level;

	private transient PlayerClass playerClass0;

	public Weapon(String name, ItemRarity rarity, Material icon, String description, String playerClass, int level) {
		super(name, rarity, icon, description);
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
		String description = getDescription();
		String lore = ChatColor.GOLD + playerClass + " Weapon" + ChatColor.GOLD + "\nLevel " + level + "\n"
				+ rarity.getColor() + rarity + " Item"
				+ (description == null ? "" : ("\n\n" + ChatColor.RESET + description)) + ChatColor.GRAY
				+ "\n\nShift-click to equip";
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	public int getLevel() {
		return level;
	}

}
