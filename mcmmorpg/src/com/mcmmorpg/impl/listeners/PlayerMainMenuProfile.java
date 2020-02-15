package com.mcmmorpg.impl.listeners;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Stores data about a player in the main menu.
 * @author conno
 *
 */
class PlayerMainMenuProfile {

	final Player player;
	private Inventory mainMenu;
	private ItemStack characterSlot1, characterSlot2, characterSlot3, characterSlot4;

	public PlayerMainMenuProfile(Player player) {
		this.player = player;
	}

	
	
}
