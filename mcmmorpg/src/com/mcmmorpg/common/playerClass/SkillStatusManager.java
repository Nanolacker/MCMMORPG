package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;

public class SkillStatusManager {

	private Map<Skill, SkillStatus> skillMap;

	public SkillStatusManager(SkillStatus[] skillStatuses) {
		skillMap = new HashMap<>();
		for (SkillStatus skillStatus : skillStatuses) {
			Skill skill = skillStatus.getSkill();
			skillMap.put(skill, skillStatus);
		}
	}
	/**
	 * Used for serialization.
	 */
	public SkillStatus[] getSkillStatuses() {
		return skillMap.values().toArray(new SkillStatus[skillMap.size()]);
	}

	/**
	 * Returns null if the specified skill is not available to the player.
	 */
	SkillStatus getSkillStatus(Skill skill) {
		return skillMap.get(skill);
	}

	void unlockSkill(Skill skill) {
		SkillStatus skillStatus = new SkillStatus(skill);
		skillMap.put(skill, skillStatus);
	}

}
