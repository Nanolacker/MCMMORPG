package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Weapon;

public class PlayerCharacterUseWeaponEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Weapon weapon;

	public PlayerCharacterUseWeaponEvent(PlayerCharacter pc, Weapon weapon) {
		this.pc = pc;
		this.weapon = weapon;
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

	public Weapon getWeapon() {
		return weapon;
	}

}
