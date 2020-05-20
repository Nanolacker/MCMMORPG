package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.ConsumableItem;

/**
 * An event called when a player uses a consumable item. Use this event to add
 * effects to consumables.
 */
public class PlayerCharacterUseConsumableItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final ConsumableItem consumable;

	public PlayerCharacterUseConsumableItemEvent(PlayerCharacter pc, ConsumableItem consumable) {
		this.pc = pc;
		this.consumable = consumable;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the player character that used the consumable item.
	 */
	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	/**
	 * Returns the item consumed in this event.
	 */
	public ConsumableItem getConsumable() {
		return consumable;
	}

}
