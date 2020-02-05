package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.Source;

public class CharacterKillEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final AbstractCharacter killed;
	private final Source killer;

	public CharacterKillEvent(AbstractCharacter killed, Source killer) {
		this.killed = killed;
		this.killer = killer;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public AbstractCharacter getKilled() {
		return killed;
	}

	public Source getKiller() {
		return killer;
	}

}
