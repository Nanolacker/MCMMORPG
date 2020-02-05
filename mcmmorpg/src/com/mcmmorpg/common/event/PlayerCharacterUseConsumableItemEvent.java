package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.ConsumableItem;

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

	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	public ConsumableItem getConsumable() {
		return consumable;
	}

}
