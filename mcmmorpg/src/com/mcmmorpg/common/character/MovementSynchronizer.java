package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Synchronizes the movement of a character and an entity. A movement
 * synchronizers mode determines its follow behavior.
 */
public class MovementSynchronizer {

	private static final double SYNC_PERIOD = 0.1;
	private static final double SIGNIFICANT_OFFSET_SQUARED = 0.01;

	private static final List<MovementSynchronizer> activeSynchronizer = new ArrayList<>();

	private AbstractCharacter character;
	private Entity entity;
	private MovementSynchronizerMode mode;

	static {
		RepeatingTask synchronizerTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < activeSynchronizer.size(); i++) {
					MovementSynchronizer synchronizer = activeSynchronizer.get(i);
					AbstractCharacter character = synchronizer.getCharacter();
					Entity entity = synchronizer.getEntity();
					Location characterLocation = character.getLocation();
					Location entityLocation = entity.getLocation();
					if (characterLocation.getWorld() != entityLocation.getWorld()
							|| entityLocation.distanceSquared(characterLocation) > SIGNIFICANT_OFFSET_SQUARED) {
						MovementSynchronizerMode mode = synchronizer.getMode();
						switch (mode) {
						case CHARACTER_FOLLOWS_ENTITY:
							character.setLocation(entityLocation);
							break;
						case ENTITY_FOLLOWS_CHARACTER:
							entity.teleport(characterLocation);
							break;
						default:
							break;
						}
					}
				}
			}
		};
		synchronizerTask.schedule();
	}

	/**
	 * Creates a new movement synchronizer for the specified character.
	 */
	public MovementSynchronizer(AbstractCharacter character, MovementSynchronizerMode syncMode) {
		this.character = character;
		this.entity = null;
		this.mode = syncMode;
	}

	/**
	 * Returns the character associated with this movement synchronizer.
	 */
	public AbstractCharacter getCharacter() {
		return character;
	}

	/**
	 * Sets the character associated with this movement synchronizer.
	 */
	public void setCharacter(AbstractCharacter character) {
		this.character = character;
	}

	/**
	 * Returns the entity associated with this movement synchronizer.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Sets the entity associated with this movement synchronizer.
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Returns whether this movement synchronizer is active and synchronizing.
	 */
	public boolean isEnabled() {
		return activeSynchronizer.contains(this);
	}

	/**
	 * Sets whether this movement synchronizer is active and synchronizing.
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			activeSynchronizer.add(this);
		} else {
			activeSynchronizer.remove(this);
		}
	}

	/**
	 * Sets the mode of this movement synchronizer.
	 */
	public void setMode(MovementSynchronizerMode syncMode) {
		this.mode = syncMode;
	}

	/**
	 * Returns the mode of this movement synchronizer.
	 */
	public MovementSynchronizerMode getMode() {
		return mode;
	}

	/**
	 * Modes by which to synchronize character and entity movement.
	 */
	public static enum MovementSynchronizerMode {
		/**
		 * The character will follow the entity.
		 */
		CHARACTER_FOLLOWS_ENTITY,
		/**
		 * The entity will follow the character.
		 */
		ENTITY_FOLLOWS_CHARACTER
	}

}
