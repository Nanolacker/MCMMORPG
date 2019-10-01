package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.time.RepeatingTask;

public abstract class NonPlayerCharacter extends CommonCharacter {

	private static final double SPAWN_PERIOD_SECONDS = 0.1;

	private static List<NonPlayerCharacter> spawningNpcs;

	private boolean spawning;
	private boolean spawned;

	public static void startSpawnTask() {
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Plugin must be uninitialized");
		}
		spawningNpcs = new ArrayList<>();
		RepeatingTask spawnTask = new RepeatingTask(SPAWN_PERIOD_SECONDS) {
			@Override
			public void run() {
				for (NonPlayerCharacter npc : spawningNpcs) {
					boolean alive = npc.isAlive();
					if (alive) {
						boolean canSpawn = npc.canSpawn();
						boolean isSpawned = npc.isSpawned();
						if (canSpawn) {
							if (!isSpawned) {
								npc.spawn();
							}
						} else {
							if (isSpawned) {
								npc.despawn();
							}
						}
					}
				}
			}
		};
		spawnTask.schedule();
	}

	/**
	 * Invoked when plugin is disabled.
	 */
	public static void despawnAll() {
		for (NonPlayerCharacter npc : spawningNpcs) {
			if (npc.isSpawned()) {
				npc.despawn();
			}
		}
	}

	protected NonPlayerCharacter(String name, int level, Location location, double maxHealth) {
		super(name, level, location, maxHealth);
		spawning = false;
		spawned = false;
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

	public final boolean isSpawned() {
		return spawned;
	}

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void spawn() {
		spawned = true;
		setNameplateVisible(true);
	}

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void despawn() {
		spawned = false;
		setNameplateVisible(false);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	protected void die() {
		super.die();
		despawn();
	}

	private static final double MAX_DISTANCE_FROM_PLAYER_TO_SPAWN = 25.0;

	/**
	 * Returns whether the conditions are suitable for this npc to spawn. By
	 * default, returns whether there is a player nearby. Override in subclasses to
	 * provide alternative functionality.
	 */
	protected boolean canSpawn() {
		Location location = getLocation();
		boolean playerIsNearby = PlayerCharacter.playerIsNearby(location, MAX_DISTANCE_FROM_PLAYER_TO_SPAWN,
				MAX_DISTANCE_FROM_PLAYER_TO_SPAWN, MAX_DISTANCE_FROM_PLAYER_TO_SPAWN);
		return playerIsNearby;
	}

}
