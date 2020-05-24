package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.Debug;

/**
 * An item to be wielded in the main hand of player characters.
 */
public class Weapon extends Item {

	private final String playerClass;
	private final int level;
	private final double baseDamage;

	private transient PlayerClass playerClass0;

	public Weapon(String name, ItemRarity rarity, Material icon, String description, String playerClass, int level,
			double baseDamage) {
		super(name, rarity, icon, description);
		this.playerClass = playerClass;
		this.level = level;
		this.baseDamage = baseDamage;
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
		StringBuilder lore = new StringBuilder();
		lore.append(rarity.getColor() + rarity.toString() + " Item\n");
		lore.append(ChatColor.GOLD + playerClass + " Weapon\n");
		lore.append("Level " + level + "\n");
		lore.append((int) baseDamage + " Base Damage\n\n");
		if (description != null) {
			lore.append(ChatColor.RESET + description + "\n\n");
		}
		lore.append(ChatColor.GRAY + "Shift-click to equip");
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore.toString(), getIcon());
	}

	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	public int getLevel() {
		return level;
	}

	public double getBaseDamage() {
		return baseDamage;
	}

}
