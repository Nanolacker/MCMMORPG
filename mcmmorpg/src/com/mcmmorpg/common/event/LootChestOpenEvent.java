package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.item.LootChest;

public class LootChestOpenEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private LootChest chest;

	public LootChestOpenEvent(LootChest chest) {
		this.chest = chest;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public LootChest getChest() {
		return chest;
	}

}
