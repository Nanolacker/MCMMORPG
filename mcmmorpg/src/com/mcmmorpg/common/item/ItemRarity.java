package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;

/**
 * Represents how rare an item is.
 */
public enum ItemRarity {

	COMMON(ChatColor.GRAY, "Common"), UNCOMMON(ChatColor.GREEN, "Uncommon"), RARE(ChatColor.BLUE,
			"Rare"), EPIC(ChatColor.LIGHT_PURPLE, "Epic"), LEGENDARY(ChatColor.GOLD, "Legendary");

	private final ChatColor color;
	private final String text;

	ItemRarity(ChatColor color, String text) {
		this.color = color;
		this.text = text;
	}

	public ChatColor getColor() {
		return color;
	}

	@Override
	public String toString() {
		return text;
	}

}
