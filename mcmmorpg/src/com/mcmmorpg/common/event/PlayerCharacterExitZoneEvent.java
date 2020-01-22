package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.Zone;
import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterExitZoneEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Zone zone;

	public PlayerCharacterExitZoneEvent(PlayerCharacter pc, Zone zone) {
		this.pc = pc;
		this.zone = zone;
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

	public Zone getZone() {
		return zone;
	}

}
