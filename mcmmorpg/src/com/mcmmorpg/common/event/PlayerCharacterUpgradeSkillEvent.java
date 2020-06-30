package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.Skill;

/**
 * An event called when a player unlocks or upgrades a skill.
 */
public class PlayerCharacterUpgradeSkillEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Skill skill;

	public PlayerCharacterUpgradeSkillEvent(PlayerCharacter pc, Skill skill) {
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

	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	public Skill getSkill() {
		return skill;
	}

	public int getNewUpgradeLevel() {
		return skill.getUpgradeLevel(pc);
	}

}
