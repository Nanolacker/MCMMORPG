package com.mcmmorpg.common.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class StaticInteractableEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack interactable;

	public StaticInteractableEvent(Player player, ItemStack interactable) {
		this.player = player;
		this.interactable = interactable;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack getInteractable() {
		return interactable;
	}

}
