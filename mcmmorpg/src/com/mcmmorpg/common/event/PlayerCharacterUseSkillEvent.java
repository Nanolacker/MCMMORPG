package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.Skill;

/**
 * An event called when a player character uses a skill. Use this event to add
 * effects to skills.
 */
public class PlayerCharacterUseSkillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final Skill skill;

    public PlayerCharacterUseSkillEvent(PlayerCharacter pc, Skill skill) {
        this.pc = pc;
        this.skill = skill;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the player character that used the skill.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    /**
     * Returns the skill used in this event.
     */
    public Skill getSkill() {
        return skill;
    }
}
