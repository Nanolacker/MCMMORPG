package com.mcmmorpg.common.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * An event called when a player interacts with a static interactable item.
 */
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

	/**
	 * Returns the player in this event.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the interactable in this event.
	 */
	public ItemStack getInteractable() {
		return interactable;
	}

}
