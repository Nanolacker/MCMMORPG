package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.Skill;

public class SkillUseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Skill skill;

	public SkillUseEvent(PlayerCharacter pc, Skill skill) {
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

	public PlayerCharacter getPlayer() {
		return pc;
	}

	public Skill getSkill() {
		return skill;
	}
}
