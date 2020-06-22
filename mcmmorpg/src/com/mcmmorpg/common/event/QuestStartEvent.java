package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;

/**
 * An event called when a player character starts a quest.
 */
public class QuestStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final Quest quest;

	public QuestStartEvent(PlayerCharacter pc, Quest quest) {
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
	 * Returns the player character that started the quest.
	 */
	public PlayerCharacter getPlayerCharacter() {
		return pc;
	}

	/**
	 * Returns the started quest.
	 */
	public Quest getQuest() {
		return quest;
	}

}
