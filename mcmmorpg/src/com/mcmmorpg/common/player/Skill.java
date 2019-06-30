package com.mcmmorpg.common.player;

public class Skill {

	private final String name;
	private final int level;
	private final int skillTreeRow;
	private final int skillTreeColumn;
	private transient PlayerClass playerClass;

	public Skill(String name, int level, int skillTreeRow, int skillTreeColumn) {
		this.name = name;
		this.level = level;
		this.skillTreeRow = skillTreeRow;
		this.skillTreeColumn = skillTreeColumn;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	void setPlayerClass(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public void addOnUseListener(SkillOnUseListener listener) {

	}

}
