package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.AbstractCharacter;

public class CharacterDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final AbstractCharacter character;

	public CharacterDeathEvent(AbstractCharacter character) {
		this.character = character;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public AbstractCharacter getCharacter() {
		return character;
	}

}
