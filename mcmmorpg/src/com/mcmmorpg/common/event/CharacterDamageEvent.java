package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.Source;

public class CharacterDamageEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final AbstractCharacter damaged;
	private final double amount;
	private final Source source;

	public CharacterDamageEvent(AbstractCharacter damaged, double amount, Source source) {
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

	public AbstractCharacter getDamaged() {
		return damaged;
	}

	public double getAmount() {
		return amount;
	}

	public Source getSource() {
		return source;
	}

}
