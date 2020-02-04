package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.NonPlayerCharacter;

public class NonPlayerCharacterSpawnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final NonPlayerCharacter npc;

	public NonPlayerCharacterSpawnEvent(NonPlayerCharacter npc) {
		this.npc = npc;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public NonPlayerCharacter getNpc() {
		return npc;
	}

}
