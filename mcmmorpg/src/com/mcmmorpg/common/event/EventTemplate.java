package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This is a template for writing custom Events.
 */
class EventTemplate extends Event {

	private static final HandlerList handlers = new HandlerList();

	// Fields go here.
	
	// Constructor goes here.

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	// Other methods go here.

}
