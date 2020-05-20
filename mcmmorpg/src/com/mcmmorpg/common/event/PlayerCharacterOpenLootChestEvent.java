package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.LootChest;

/**
 * An event called when a player character opens a loot chest.
 */
public class PlayerCharacterOpenLootChestEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter whoOpened;
	private final LootChest lootChest;

	public PlayerCharacterOpenLootChestEvent(PlayerCharacter whoOpened, LootChest chest) {
		this.whoOpened = whoOpened;
		this.lootChest = chest;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the player character who opened the loot chest in this event.
	 */
	public PlayerCharacter getWhoOpened() {
		return whoOpened;
	}

	/**
	 * Returns the loot chest opened in this event.
	 */
	public LootChest getLootChest() {
		return lootChest;
	}

}
