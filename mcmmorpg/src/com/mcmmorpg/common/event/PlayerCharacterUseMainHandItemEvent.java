package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.MainHandItem;

public class PlayerCharacterUseMainHandItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final MainHandItem mainHand;

	public PlayerCharacterUseMainHandItemEvent(PlayerCharacter pc, MainHandItem mainHand) {
		this.pc = pc;
		this.mainHand = mainHand;
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

	public MainHandItem getMainHand() {
		return mainHand;
	}

}
