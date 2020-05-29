package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.playerClass.PlayerClass;

/**
 * An item that can be equipped by a player character to provide protections.
 */
public class ArmorItem extends Item {

	private final String playerClass;
	private final int level;
	private final ArmorType type;
	private final double protections;

	private transient PlayerClass playerClass0;

	/**
	 * Create a new armor item. initialize() must be called after construction.
	 */
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

	/**
	 * Returns the player class that can equip this armor item.
	 */
	public PlayerClass getPlayerClass() {
		return playerClass0;
	}

	/**
	 * Returns the minimum level required for a player character to equip this armor
	 * item.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns the type or equipment slot of this armor item.
	 */
	public ArmorType getType() {
		return type;
	}

	/**
	 * Returns how many protections this armor item provides to player characters
	 * who equip it.
	 */
	public double getProtections() {
		return protections;
	}

	/**
	 * A type, or equipment slot of an armor item.
	 */
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
