package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

/**
 * An item that can be equipped to provide protections.
 */
public class ArmorItem extends Item {

	private final String playerClass;
	private final int level;
	private final ArmorType type;
	private final double protections;

	private transient PlayerClass playerClass0;

	public ArmorItem(String name, ItemRarity rarity, Material icon, String description, String playerClass, int level,
			ArmorType type, int protections) {
		super(name, rarity, icon, description);
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
		String description = getDescription();
		StringBuilder lore = new StringBuilder();
		lore.append(rarity.getColor() + rarity.toString() + " Item\n");
		lore.append(ChatColor.GOLD + playerClass + " Armor\n");
		lore.append("Level " + level + "\n");
		lore.append((int) protections + " Protections\n\n");
		if (description != null) {
			lore.append(ChatColor.RESET + description + "\n\n");
		}
		lore.append(ChatColor.GRAY + "Shift-click to equip/unequip");
		return ItemFactory.createItemStack(rarity.getColor() + getName(), lore.toString(), getIcon());
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
		FEET("Feet"), LEGS("Legs"), CHEST("Chest"), HEAD("Head");

		private final String text;

		ArmorType(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

}
