package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class ArmorItem extends Item {

	private final String playerClass;
	private final int level;
	private final ArmorType type;
	private final double protections;

	private transient PlayerClass playerClass0;

	public ArmorItem(int id, String name, ItemRarity rarity, Material icon, String description, String playerClass,
			int level, ArmorType type, int protections) {
		super(id, name, rarity, icon, description);
		this.playerClass = playerClass;
		this.level = level;
		this.type = type;
		this.protections = protections;
	}

	@Override
	public void initialize() {
		super.initialize();
		playerClass0 = PlayerClass.forName(playerClass);
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String lore = ChatColor.GOLD + playerClass + " Armor Item" + ChatColor.GOLD + "\nLevel " + level + "\n"
				+ (int) protections + " Protections\n" + rarity.getColor() + rarity + " Item\n\n" + ChatColor.RESET
				+ getDescription() + ChatColor.GRAY + "\n\nShift-click to equip/unequip";
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	public int getLevel() {
		return level;
	}

	public ArmorType getType() {
		return type;
	}

	public double getProtections() {
		return protections;
	}

	public static enum ArmorType {
		FEET, LEGS, CHEST, HEAD
	}

}
