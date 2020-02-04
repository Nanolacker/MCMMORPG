package com.mcmmorpg.common.playerClass;

public class PlayerSkillData {

	private final String skillName;
	private final String playerClassName;
	private int upgradeLevel;
	private double skillCooldownSeconds;

	public PlayerSkillData(Skill skill) {
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

	public void setUpgradeLevel(int upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
	}

	double getSkillCooldownSeconds() {
		return skillCooldownSeconds;
	}

	void setCooldown(double cooldownSeconds) {
		this.skillCooldownSeconds = cooldownSeconds;
	}

}
