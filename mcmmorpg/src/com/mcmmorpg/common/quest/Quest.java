package com.mcmmorpg.common.quest;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;

public class Quest {

	private static final Map<String, Quest> quests;

	static {
		quests = new HashMap<>();
	}

	private final String name;
	private final int recommendedLevel;
	private final QuestPhase[] phases;

	public Quest(String name, int recommendedLevel, QuestPhase[] phases) {
		this.name = name;
		this.recommendedLevel = recommendedLevel;
		this.phases = phases;
	}

	public void initialize() {
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Cannot initialize a quest after the plugin has been initialized.");
		}
		for (int i = 0; i < phases.length; i++) {
			QuestPhase phase = phases[i];
			phase.initialize(this, i);
		}
		quests.put(name, this);
	}

	public static Quest forName(String name) {
		return quests.get(name);
	}

	public String getName() {
		return name;
	}

	public int getRecommendedLevel() {
		return recommendedLevel;
	}

	public QuestPhase[] getPhases() {
		return phases;
	}

	/**
	 * Convenience method for getting an objective in this quest.
	 * 
	 * @param iPhase          the index of the objective's parent quest phase in
	 *                        this quest's phases
	 * @param iObjectiveChain the index of the objective's parent objective chain in
	 *                        the phase's objective chains
	 * @param iObjective      the index of the objective in the objective chain's
	 *                        objectives
	 */
	public QuestObjective getObjective(int iPhase, int iObjectiveChain, int iObjective) {
		QuestPhase phase = phases[iPhase];
		QuestObjectiveChain[] objectiveChains = phase.getObjectiveChains();
		QuestObjectiveChain objectiveChain = objectiveChains[iObjectiveChain];
		QuestObjective[] objectives = objectiveChain.getObjectives();
		return objectives[iObjective];
	}

	public void makeAvailable(PlayerCharacter pc) {
		pc.getQuestStatusManager().makeQuestAvailable(this);
	}

}
