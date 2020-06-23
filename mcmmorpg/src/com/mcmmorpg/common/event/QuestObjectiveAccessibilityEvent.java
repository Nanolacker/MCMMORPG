package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestObjective;

/**
 * This is a template for writing events.
 */
public class QuestObjectiveAccessibilityEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final QuestObjective objective;
	private final boolean accessible;

	public QuestObjectiveAccessibilityEvent(PlayerCharacter pc, QuestObjective objective, boolean accessible) {
		this.pc = pc;
		this.objective = objective;
		this.accessible = accessible;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Return the player for which the objective's accessibility was modified.
	 */
	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	/**
	 * Returns the objective whose accessibility was modified.
	 */
	public QuestObjective getObjective() {
		return objective;
	}

	/**
	 * Returns whether the quest objective was made accessible.
	 */
	public boolean isObjectiveAccessible() {
		return accessible;
	}

}
