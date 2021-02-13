package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * An event called when a player character is registered (i.e. when a player
 * character joins the game).
 */
public class PlayerCharacterRegisterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;

    public PlayerCharacterRegisterEvent(PlayerCharacter pc) {
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
     * Returns the registered player character.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
