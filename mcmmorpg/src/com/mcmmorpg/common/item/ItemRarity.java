package com.mcmmorpg.common.item;

import org.bukkit.ChatColor;

public enum ItemRarity {

	POOR(ChatColor.GRAY, "Poor"), COMMON(ChatColor.WHITE, "White"), UNCOMMON(ChatColor.GREEN, "Green"),
	RARE(ChatColor.BLUE, "Rare"), EPIC(ChatColor.LIGHT_PURPLE, "Epic"), LEGENDARY(ChatColor.GOLD, "Legendary");

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
