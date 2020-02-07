package com.mcmmorpg.common.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

public class ArmorItem extends Item {

	private final String playerClass;
	private final int level;
	private final double protections;

	private transient PlayerClass playerClass0;

	public ArmorItem(int id, String name, ItemRarity rarity, Material icon, String description, String playerClass,
			int level, double protections) {
		super(id, name, rarity, icon, description);
		this.playerClass = playerClass;
		this.level = level;
		this.protections = protections;
	}

	@Override
	protected void initialize() {
		super.initialize();
		playerClass0 = PlayerClass.forName(playerClass);
	}

	@Override
	protected ItemStack createItemStack() {
		ItemRarity rarity = getRarity();
		String lore = rarity + " Armor\n" + getDescription();
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore, getIcon());
	}

	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	public int getLevel() {
		return level;
	}

	public double getProtections() {
		return protections;
	}

}
