package com.mcmmorpg.common.playerClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * Manages data for a player character's skills.
 */
public class PlayerCharacterSkillManager {

	private final PlayerCharacter pc;
	private final Map<Skill, PlayerCharacterSkillData> skillDataMap;

	/**
	 * Create a new skill manager for the specified player character using previous
	 * skill data.
	 */
	public PlayerCharacterSkillManager(PlayerCharacter pc, PlayerCharacterSkillData[] allSkillData) {
		this.pc = pc;
		skillDataMap = new HashMap<>();
		for (PlayerCharacterSkillData skillData : allSkillData) {
			Skill skill = skillData.getSkill();
			skillDataMap.put(skill, skillData);
		}
	}

	public void init() {
		Collection<PlayerCharacterSkillData> allSkillData = skillDataMap.values();
		for (PlayerCharacterSkillData skillData : allSkillData) {
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
	public PlayerCharacterSkillData[] getAllSkillData() {
		return skillDataMap.values().toArray(new PlayerCharacterSkillData[skillDataMap.size()]);
	}

	/**
	 * Returns null if the specified skill is not available to the player.
	 */
	PlayerCharacterSkillData getSkillData(Skill skill) {
		return skillDataMap.get(skill);
	}

	void unlockSkill(Skill skill) {
		PlayerCharacterSkillData skillStatus = new PlayerCharacterSkillData(skill);
		skillDataMap.put(skill, skillStatus);
	}

}
