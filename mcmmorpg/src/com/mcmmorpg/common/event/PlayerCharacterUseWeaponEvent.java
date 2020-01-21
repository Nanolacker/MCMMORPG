package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterUseWeaponEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final ItemStack weapon;

	public PlayerCharacterUseWeaponEvent(PlayerCharacter pc, ItemStack weapon) {
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

	public ItemStack getWeapon() {
		return weapon;
	}

}
