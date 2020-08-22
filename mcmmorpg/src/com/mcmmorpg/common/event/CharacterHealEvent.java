package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.character.Source;

/**
 * An event called whenever a character is healed by a source.
 */
public class CharacterHealEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Character healed;
	private final double amount;
	private final Source source;

	public CharacterHealEvent(Character healed, double amount, Source source) {
		this.healed = healed;
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
	 * Returns the character healed in the event.
	 */
	public Character getHealed() {
		return healed;
	}

	/**
	 * Returns how much healing the healed character received in the event.
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Returns the source of the healing.
	 */
	public Source getSource() {
		return source;
	}

}
