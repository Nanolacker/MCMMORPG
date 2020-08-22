package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.character.Source;

/**
 * An event called whenever a character is damaged by a source.
 */
public class CharacterDamageEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Character damaged;
	private final double amount;
	private final Source source;

	public CharacterDamageEvent(Character damaged, double amount, Source source) {
		this.damaged = damaged;
		this.amount = amount;
		this.source = source;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the character damaged in the event.
	 */
	public Character getDamaged() {
		return damaged;
	}

	/**
	 * Returns how much damaged the damaged character received in the event.
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Returns the source of the damage.
	 */
	public Source getSource() {
		return source;
	}

}
