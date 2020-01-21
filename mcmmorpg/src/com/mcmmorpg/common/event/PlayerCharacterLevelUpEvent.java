package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterLevelUpEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final int newLevel;

	public PlayerCharacterLevelUpEvent(PlayerCharacter pc, int newLevel) {
		this.pc = pc;
		this.newLevel = newLevel;
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

	public int getNewLevel() {
		return newLevel;
	}

}
