package com.mcmmorpg.common.playerClass;

public class SkillStatus {

	private final String skillName;
	private final String playerClassName;
	private int skillLevel;
	private double skillCooldownSeconds;

	public SkillStatus(Skill skill) {
		skillName = skill.getName();
		playerClassName = skill.getPlayerClass().getName();
		skillLevel = 0;
		skillCooldownSeconds = 0.0;
	}

	public Skill getSkill() {
		PlayerClass playerClass = PlayerClass.forName(playerClassName);
		return playerClass.skillForName(skillName);
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public double getSkillCooldownSeconds() {
		return skillCooldownSeconds;
	}

	public void setCooldownSeconds(double cooldownSeconds) {
		this.skillCooldownSeconds = cooldownSeconds;
	}

}
