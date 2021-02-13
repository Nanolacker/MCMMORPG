package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.Skill;

/**
 * An event called when a player adds a skill to their hotbar.
 */
public class PlayerCharacterAddSkillToHotbarEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final Skill skill;
    private final int hotbarSlot;

    public PlayerCharacterAddSkillToHotbarEvent(PlayerCharacter pc, Skill skill, int hotbarSlot) {
        this.pc = pc;
        this.skill = skill;
        this.hotbarSlot = hotbarSlot;
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

    public Skill getSkill() {
        return skill;
    }

    public int getHotbarSlot() {
        return hotbarSlot;
    }
}
