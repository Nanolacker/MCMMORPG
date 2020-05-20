package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.NonPlayerCharacterDespawnEvent;
import com.mcmmorpg.common.event.NonPlayerCharacterSpawnEvent;
import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Represents an NPC. Methods can be overridden in subclasses and should invoke
 * super.
 */
public class NonPlayerCharacter extends AbstractCharacter {

	private static final double SPAWN_PERIOD_SECONDS = 1.0;
	private static final double DEFAULT_SPAWN_RADIUS = 50.0;
	private static final double DEFAULT_DESPAWN_RADIUS = 55.0;

	private static List<NonPlayerCharacter> aliveNpcs = new ArrayList<>();

	private boolean spawned;

	/**
	 * Invoked when plugin is enabled.
	 */
	public static void startNPCSpawner() {
		RepeatingTask npcSpawner = new RepeatingTask(SPAWN_PERIOD_SECONDS) {
			@Override
			public void run() {
				for (int i = 0; i < aliveNpcs.size(); i++) {
					NonPlayerCharacter npc = aliveNpcs.get(i);
					boolean alive = npc.isAlive();
					if (alive) {
						if (npc.isSpawned()) {
							if (npc.shouldDespawn()) {
								npc.despawn();
							}
						} else {
							if (npc.shouldSpawn()) {
								npc.spawn();
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

	/**
	 * Creates a new NPC.
	 */
	protected NonPlayerCharacter(String name, int level, Location location) {
		super(name, level, location);
		spawned = false;
	}

	/**
	 * Returns true if this NPC is spawned. Returns false if this NPC is despawned
	 * or dead.
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
		NonPlayerCharacterSpawnEvent event = new NonPlayerCharacterSpawnEvent(this);
		EventManager.callEvent(event);
	}

	/**
	 * Called when the NPC spawner deems it appropriate to despawn this NPC. This
	 * method is NOT called when this NPC dies. Additional functionality may be
	 * specified in subclasses. Overriding methods must invoke super.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void despawn() {
		spawned = false;
		setNameplateVisible(false);
		NonPlayerCharacterDespawnEvent event = new NonPlayerCharacterDespawnEvent(this);
		EventManager.callEvent(event);
	}

	/**
	 * Called when this NPC becomes alive.
	 */
	@OverridingMethodsMustInvokeSuper
	@Override
	protected void onLive() {
		super.onLive();
		aliveNpcs.add(this);
	}

	/**
	 * Called when this NPC dies.
	 */
	@OverridingMethodsMustInvokeSuper
	@Override
	protected void onDeath() {
		super.onDeath();
		aliveNpcs.remove(this);
		spawned = false;
	}

	/**
	 * Returns whether the conditions are suitable for this NPC to spawn. By
	 * default, returns whether there is a player nearby. Override in subclasses to
	 * provide alternative functionality.
	 */
	protected boolean shouldSpawn() {
		Location location = getLocation();
		boolean playerIsNearby = PlayerCharacter.playerCharacterIsNearby(location, DEFAULT_SPAWN_RADIUS);
		return playerIsNearby;
	}

	/**
	 * Returns whether the conditions are suitable for this NPC to despawn. By
	 * default, returns whether there is not a player nearby. Override in subclasses
	 * to provide alternative functionality.
	 */
	protected boolean shouldDespawn() {
		Location location = getLocation();
		boolean playerIsNearby = PlayerCharacter.playerCharacterIsNearby(location, DEFAULT_DESPAWN_RADIUS);
		return !playerIsNearby;
	}

}
