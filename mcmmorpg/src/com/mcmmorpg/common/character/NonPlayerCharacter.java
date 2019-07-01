package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

public abstract class NonPlayerCharacter extends CommonCharacter {

	private static List<NonPlayerCharacter> spawningNpcs;

	private boolean alive;
	private boolean spawning;
	private boolean spawned;

	static {
		spawningNpcs = new ArrayList<>();
	}

	protected NonPlayerCharacter(String name, int level, Location location) {
		super(name, level, location);
		alive = true;
		spawning = false;
		spawned = false;
	}

	public final boolean getAlive() {
		return alive;
	}

	@OverridingMethodsMustInvokeSuper
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public final boolean getSpawning() {
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

	public final boolean getSpawned() {
		return spawned;
	}

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void spawn() {
		spawned = true;
	}

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void despawn() {
		spawned = false;
	}

}
