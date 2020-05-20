package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.NonPlayerCharacter;

/**
 * An event called when an NPC despawns;
 */
public class NonPlayerCharacterDespawnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final NonPlayerCharacter npc;

	public NonPlayerCharacterDespawnEvent(NonPlayerCharacter npc) {
		this.npc = npc;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the NPC that despawned in this event.
	 */
	public NonPlayerCharacter getNpc() {
		return npc;
	}

}
