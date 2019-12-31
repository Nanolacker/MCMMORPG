package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;

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

	public PlayerCharacter getPlayer() {
		return pc;
	}

	public Quest getQuest() {
		return quest;
	}

}
