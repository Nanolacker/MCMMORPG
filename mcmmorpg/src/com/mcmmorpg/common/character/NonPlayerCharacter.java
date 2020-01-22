package com.mcmmorpg.common.character;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Represents an NPC. Methods can be overridden in subclasses and should invoke
 * super.
 */
public abstract class NonPlayerCharacter extends AbstractCharacter {

	private static final double SPAWN_PERIOD_SECONDS = 0.1;

	private static Set<NonPlayerCharacter> aliveNpcs = new HashSet<>();

	private boolean spawned;

	public static void startNPCSpawner() {
		if (MMORPGPlugin.isInitialized()) {
			throw new IllegalStateException("Plugin must be uninitialized");
		}
		RepeatingTask npcSpawner = new RepeatingTask(SPAWN_PERIOD_SECONDS) {
			@Override
			public void run() {
				for (NonPlayerCharacter npc : aliveNpcs) {
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
		npcSpawner.schedule();
	}

	/**
	 * Invoked when plugin is disabled.
	 */
	public static void despawnAll() {
		for (NonPlayerCharacter npc : aliveNpcs) {
			if (npc.isSpawned()) {
				npc.despawn();
			}
		}
	}

	protected NonPlayerCharacter(String name, int level, Location location, double maxHealth) {
		super(name, level, location, maxHealth);
		spawned = false;
	}

	/**
	 * Returns whether this NPC is spawned.
	 */
	public final boolean isSpawned() {
		return spawned;
	}

	/**
	 * Invoked when the NPC spawner deems it appropriate to spawn this NPC.
	 * Additional functionality may be specified in subclasses. Overriding methods
	 * must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void spawn() {
		spawned = true;
		setNameplateVisible(true);
	}

	/**
	 * Called when the NPC spawner deems it appropriate to despawn this NPC.
	 * Additional functionality may be specified in subclasses. Overriding methods
	 * must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void despawn() {
		spawned = false;
		setNameplateVisible(false);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	protected void onLive() {
		aliveNpcs.add(this);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	protected void onDeath() {
		aliveNpcs.remove(this);
		despawn();
	}

	private static final double DEFAULT_SPAWN_RADIUS = 25.0;

	/**
	 * Returns whether the conditions are suitable for this NPC to spawn. By
	 * default, returns whether there is a player nearby. Override in subclasses to
	 * provide alternative functionality.
	 */
	protected boolean canSpawn() {
		Location location = getLocation();
		boolean playerIsNearby = PlayerCharacter.playerIsNearby(location, DEFAULT_SPAWN_RADIUS, DEFAULT_SPAWN_RADIUS,
				DEFAULT_SPAWN_RADIUS);
		return playerIsNearby;
	}

}
