package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.PlayerQuestData;

public class SkillStatusManager {

	private Map<Skill, PlayerSkillStatus> skillMap;

	public SkillStatusManager(PlayerSkillStatus[] skillStatuses) {
		skillMap = new HashMap<>();
		for (PlayerSkillStatus skillStatus : skillStatuses) {
			Skill skill = skillStatus.getSkill();
			skillMap.put(skill, skillStatus);
		}
	}
	/**
	 * Used for save data.
	 */
	public PlayerSkillStatus[] getSkillStatuses() {
		return skillMap.values().toArray(new PlayerSkillStatus[skillMap.size()]);
	}

	/**
	 * Returns null if the specified skill is not available to the player.
	 */
	PlayerSkillStatus getSkillStatus(Skill skill) {
		return skillMap.get(skill);
	}

	void unlockSkill(Skill skill) {
		PlayerSkillStatus skillStatus = new PlayerSkillStatus(skill);
		skillMap.put(skill, skillStatus);
	}

}
