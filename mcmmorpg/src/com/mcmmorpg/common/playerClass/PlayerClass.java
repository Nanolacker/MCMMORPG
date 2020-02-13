package com.mcmmorpg.common.playerClass;

import java.util.HashMap;
import java.util.Map;

/**
 * PlayerClasses can be made using JSON. Code will need to be written that deals
 * with the class's skill use, however.
 */
public final class PlayerClass {

	/**
	 * Keys are the names of the classes.
	 */
	private static final Map<String, PlayerClass> playerClasses;

	private final String name;
	private final Skill[] skills;
	private transient SkillTree skillTree;

	static {
		playerClasses = new HashMap<>();
	}

	public PlayerClass(String name, Skill[] skills) {
		this.name = name;
		this.skills = skills;
	}

	public static PlayerClass forName(String name) {
		return playerClasses.get(name);
	}

	public void initialize() {
		for (Skill skill : skills) {
			skill.initialize(this);
		}
		this.skillTree = new SkillTree(this);
		playerClasses.put(name, this);
	}

	public String getName() {
		return name;
	}

	public Skill[] getSkills() {
		return skills;
	}

	public SkillTree getSkillTree() {
		return skillTree;
	}

	/**
	 * Returns null if no skill with the specified name exists.
	 */
	public Skill skillForName(String skillName) {
		for (Skill skill : skills) {
			if (skill.getName().equals(skillName)) {
				return skill;
			}
		}
		return null;
	}

}
