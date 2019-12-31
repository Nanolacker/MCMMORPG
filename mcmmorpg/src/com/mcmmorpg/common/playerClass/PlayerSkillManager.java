package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.PlayerQuestData;

public class PlayerSkillManager {

	private Map<Skill, PlayerSkillData> skillDataMap;

	public PlayerSkillManager(PlayerSkillData[] allSkillData) {
		skillDataMap = new HashMap<>();
		for (PlayerSkillData skillData : allSkillData) {
			Skill skill = skillData.getSkill();
			skillDataMap.put(skill, skillData);
		}
	}
	/**
	 * Used for save data.
	 */
	public PlayerSkillData[] getAllSkillData() {
		return skillDataMap.values().toArray(new PlayerSkillData[skillDataMap.size()]);
	}

	/**
	 * Returns null if the specified skill is not available to the player.
	 */
	PlayerSkillData getSkillData(Skill skill) {
		return skillDataMap.get(skill);
	}

	void unlockSkill(Skill skill) {
		PlayerSkillData skillStatus = new PlayerSkillData(skill);
		skillDataMap.put(skill, skillStatus);
	}

}
