package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterUnequipItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final ItemStack item;
	private final EquipmentSlot equipmentType;

	public PlayerCharacterUnequipItemEvent(PlayerCharacter pc, ItemStack item, EquipmentSlot equipmentType) {
		this.pc = pc;
		this.item = item;
		this.equipmentType = equipmentType;
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

	public ItemStack getItem() {
		return item;
	}

	public EquipmentSlot getEquipmentType() {
		return equipmentType;
	}

}
