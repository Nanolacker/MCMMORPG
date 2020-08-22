package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.Character;

/**
 * An event called whenever a character dies.
 */
public class CharacterDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Character character;

	public CharacterDeathEvent(Character character) {
		this.character = character;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the character that died in the event.
	 */
	public Character getCharacter() {
		return character;
	}

}
