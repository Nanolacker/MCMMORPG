package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestObjective;

/**
 * An event called when a player character's progress toward a quest objective
 * is changed.
 */
public class QuestObjectiveChangeProgressEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final QuestObjective objective;
    private final int previousProgress;
    private final int newProgress;

    public QuestObjectiveChangeProgressEvent(PlayerCharacter pc, QuestObjective objective, int previousProgress,
            int newProgress) {
        this.pc = pc;
        this.objective = objective;
        this.previousProgress = previousProgress;
        this.newProgress = newProgress;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the player character in this event.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    /**
     * Returns the quest objective in this event.
     */
    public QuestObjective getObjective() {
        return objective;
    }

    /**
     * Returns the progress the player character had previously.
     */
    public int getPreviousProgress() {
        return previousProgress;
    }

    /**
     * Returns the progress the player character has now.
     */
    public int getNewProgress() {
        return newProgress;
    }
}
