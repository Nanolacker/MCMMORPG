package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * An event called when a player character is removed (i.e. when a player
 * character leaves the game).
 */
public class PlayerCharacterRemoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;

    public PlayerCharacterRemoveEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the removed player character.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
