package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;

/**
 * An event called when a player character completes a quest.
 */
public class QuestCompletionEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Quest quest;

	public QuestCompletionEvent(PlayerCharacter pc, Quest quest) {
		this.pc = pc;
		this.quest = quest;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the player character that completed the quest.
	 */
	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	/**
	 * Returns the completed quest.
	 */
	public Quest getQuest() {
		return quest;
	}

}
