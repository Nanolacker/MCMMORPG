package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * An event called when a player character levels up.
 */
public class PlayerCharacterLevelUpEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final int newLevel;

    public PlayerCharacterLevelUpEvent(PlayerCharacter pc, int newLevel) {
        this.pc = pc;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the player character that leveled up in this event.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    /**
     * Returns the level that the player character reached in this event.
     */
    public int getNewLevel() {
        return newLevel;
    }
}
