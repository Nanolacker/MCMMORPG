package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterOpenQuestLogEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;

    public PlayerCharacterOpenQuestLogEvent(PlayerCharacter pc) {
        this.pc = pc;
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
}
