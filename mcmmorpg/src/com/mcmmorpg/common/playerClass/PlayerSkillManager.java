package com.mcmmorpg.common.playerClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * Manages data for a player character's skills.
 */
public class PlayerSkillManager {

	private final PlayerCharacter pc;
	private final Map<Skill, PlayerSkillData> skillDataMap;

	/**
	 * Create a new skill manager for the specified player character using previous
	 * skill data.
	 */
	public PlayerSkillManager(PlayerCharacter pc, PlayerSkillData[] allSkillData) {
		this.pc = pc;
		skillDataMap = new HashMap<>();
		for (PlayerSkillData skillData : allSkillData) {
			Skill skill = skillData.getSkill();
			skillDataMap.put(skill, skillData);
		}
	}

	public void init() {
		Collection<PlayerSkillData> allSkillData = skillDataMap.values();
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
