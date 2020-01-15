package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

public class ConsumableItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private final PlayerCharacter consumer;
	private final ItemStack consumable;

	public ConsumableItemEvent(PlayerCharacter consumer, ItemStack consumable) {
		this.consumer = consumer;
		this.consumable = consumable;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public PlayerCharacter getConsumer() {
		return consumer;
	}

	public ItemStack getConsumable() {
		return consumable;
	}

}
