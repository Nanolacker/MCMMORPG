package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestObjective;

public class PlayerQuestObjectiveChangeProgressEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerCharacter pc;
	private final QuestObjective objective;
	private final int previousProgress;
	private final int newProgress;

	public PlayerQuestObjectiveChangeProgressEvent(PlayerCharacter pc, QuestObjective objective, int previousProgress,
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

	public PlayerCharacter getPlayer() {
		return pc;
	}

	public QuestObjective getObjective() {
		return objective;
	}

	public int getPreviousProgress() {
		return previousProgress;
	}

	public int getNewProgress() {
		return newProgress;
	}

}
