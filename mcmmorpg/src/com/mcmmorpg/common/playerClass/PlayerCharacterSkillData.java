package com.mcmmorpg.common.playerClass;

/**
 * A serializable data container that stores player character data pertaining to
 * a skill, such as upgrade level and cooldown.
 */
public class PlayerCharacterSkillData {

	private final String skillName;
	private final String playerClassName;
	private int upgradeLevel;
	private double skillCooldownSeconds;

	PlayerCharacterSkillData(Skill skill) {
		skillName = skill.getName();
		playerClassName = skill.getPlayerClass().getName();
		upgradeLevel = 1;
		skillCooldownSeconds = 0.0;
	}

	Skill getSkill() {
		PlayerClass playerClass = PlayerClass.forName(playerClassName);
		return playerClass.skillForName(skillName);
	}

	int getUpgradeLevel() {
		return upgradeLevel;
	}

	void setUpgradeLevel(int upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
	}

	double getSkillCooldownSeconds() {
		return skillCooldownSeconds;
	}

	void setCooldown(double cooldownSeconds) {
		this.skillCooldownSeconds = cooldownSeconds;
	}

}
