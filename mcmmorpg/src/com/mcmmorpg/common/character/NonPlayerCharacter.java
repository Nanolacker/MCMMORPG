package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public abstract class NonPlayerCharacter extends CommonCharacter {

	private static List<NonPlayerCharacter> spawningNpcs;

	private boolean spawning;

	static {
		spawningNpcs = new ArrayList<>();
	}

	protected NonPlayerCharacter(String name, int level, Location location) {
		super(name, level, location);
	}

	public boolean isSpawning() {
		return spawning;
	}

	/**
	 * If an npc is spawning, it will spawn when a player is nearby.
	 */
	public void setSpawning(boolean spawning) {
		boolean redundant = this.spawning == spawning;
		if (redundant) {
			return;
		}
		this.spawning = spawning;
		if (spawning) {
			spawningNpcs.add(this);
		} else {
			spawningNpcs.remove(this);
		}
	}

	public abstract void spawn();

}
