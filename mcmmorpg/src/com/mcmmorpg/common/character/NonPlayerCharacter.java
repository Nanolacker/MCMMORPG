package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

public abstract class NonPlayerCharacter extends CommonCharacter {

	private static List<NonPlayerCharacter> spawningNpcs;

	private boolean spawning;
	private boolean spawned;

	static {
		spawningNpcs = new ArrayList<>();
	}

	protected NonPlayerCharacter(String name, int level, Location location) {
		super(name, level, location);
	}

	public final boolean isSpawning() {
		return spawning;
	}

	/**
	 * If an npc is spawning, it will spawn when a player is nearby.
	 */
	public final void setSpawning(boolean spawning) {
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

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	public void spawn() {
		spawned = true;
	}

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	public void despawn() {
		spawned = false;
	}

}
