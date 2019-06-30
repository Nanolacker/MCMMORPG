package com.mcmmorpg.common.player;

import java.util.HashMap;
import java.util.Map;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * PlayerClasses can be made using JSON. Code will need to be written that deals
 * with the class's skill use, however.
 */
public class PlayerClass {

	/**
	 * Keys are the names of the classes.
	 */
	private static final Map<String, PlayerClass> playerClasses;

	static {
		playerClasses = new HashMap<>();
	}

	private final String name;
	private Skill[] skills;

	public PlayerClass(String name) {
		this.name = name;
	}

	public static PlayerClass forName(String name) {
		return playerClasses.get(name);
	}

	public void initialize() {
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Cannot initialize a player class after the plugin has been initialized.");
		}
		for (Skill skill : skills) {
			skill.setPlayerClass(this);
		}
		playerClasses.put(name, this);
	}

	public String getName() {
		return name;
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

	public void openSkillTree(PlayerCharacter pc) {

	}
}
