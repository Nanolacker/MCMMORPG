package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.character.Source;

/**
 * An event called when a character is killed by a source.
 */
public class CharacterKillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Character killed;
    private final Source killer;

    public CharacterKillEvent(Character killed, Source killer) {
        this.killed = killed;
        this.killer = killer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the character that was killed in the event.
     */
    public Character getKilled() {
        return killed;
    }

    /**
     * Returns the source that killed the character in the event.
     */
    public Source getKiller() {
        return killer;
    }
}
