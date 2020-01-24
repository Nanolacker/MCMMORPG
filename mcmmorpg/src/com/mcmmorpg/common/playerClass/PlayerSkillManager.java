package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerSkillManager {

	private Map<Skill, PlayerSkillData> skillDataMap;

	public PlayerSkillManager(PlayerCharacter pc, PlayerSkillData[] allSkillData) {
		skillDataMap = new HashMap<>();
		// to account for players who logged out with skills on cooldown
		for (PlayerSkillData skillData : allSkillData) {
			Skill skill = skillData.getSkill();
			double cooldown = skillData.getSkillCooldownSeconds();
			if (cooldown > 0) {
				skill.cooldown(pc, cooldown);
			}
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
