package com.mcmmorpg.impl.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterOpenMenuEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;

	public PlayerCharacterOpenMenuEvent(PlayerCharacter pc) {
		this.pc = pc;
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

}
