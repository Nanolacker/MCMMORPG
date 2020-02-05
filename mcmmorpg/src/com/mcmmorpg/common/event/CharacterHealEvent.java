package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.Source;

public class CharacterHealEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final AbstractCharacter healed;
	private final double amount;
	private final Source source;

	public CharacterHealEvent(AbstractCharacter healed, double amount, Source source) {
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

	public AbstractCharacter getHealed() {
		return healed;
	}

	public double getAmount() {
		return amount;
	}

	public Source getSource() {
		return source;
	}

}
