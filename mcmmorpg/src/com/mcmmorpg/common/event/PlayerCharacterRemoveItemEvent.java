package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;

/**
 * An event called when a player receives an item via
 * PlayerCharacter.giveItem().
 */
public class PlayerCharacterRemoveItemEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final Item item;
    private final int amount;

    public PlayerCharacterRemoveItemEvent(PlayerCharacter pc, Item item, int amount) {
        this.pc = pc;
        this.item = item;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the player character involved in this event.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    /**
     * Returns the item that was received.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Returns how much of the item was received.
     */
    public int getAmount() {
        return amount;
    }
}
