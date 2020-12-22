package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Synchronizes the location of a character and an entity. A location
 * synchronizer's mode determines its follow behavior.
 */
public class LocationSynchronizer {

	private static final double SYNC_PERIOD = 0.1;
	private static final double SIGNIFICANT_POSITION_OFFSET_SQUARED = 0.01;
	private static final double SIGNIFICANT_ANGLE_OFFSET = Math.toRadians(5);

	private static final List<LocationSynchronizer> activeSynchronizers = new ArrayList<>();

	private Character character;
	private Entity entity;
	private Mode mode;

	static {
		RepeatingTask synchronizerTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < activeSynchronizers.size(); i++) {
					LocationSynchronizer synchronizer = activeSynchronizers.get(i);
					synchronizer.updateLocation();
				}
			}
		};
		synchronizerTask.schedule();
	}

	/**
	 * Creates a new location synchronizer for the specified character.
	 */
	public LocationSynchronizer(Character character, Mode syncMode) {
		this.character = character;
		this.entity = null;
		this.mode = syncMode;
	}

	/**
	 * Returns the character associated with this location synchronizer.
	 */
	public Character getCharacter() {
		return character;
	}

	/**
	 * Sets the character associated with this location synchronizer.
	 */
	public void setCharacter(Character character) {
		this.character = character;
	}

	/**
	 * Returns the entity associated with this location synchronizer.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Sets the entity associated with this location synchronizer.
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Returns whether this location synchronizer is active and synchronizing.
	 */
	public boolean isEnabled() {
		return activeSynchronizers.contains(this);
	}

	/**
	 * Sets whether this location synchronizer is active and synchronizing.
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			activeSynchronizers.add(this);
		} else {
			activeSynchronizers.remove(this);
		}
	}

	/**
	 * Returns the mode of this location synchronizer.
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets the mode of this location synchronizer.
	 */
	public void setMode(Mode syncMode) {
		this.mode = syncMode;
	}

	private void updateLocation() {
		Location characterLocation = character.getLocation();
		Location entityLocation = entity.getLocation();

		double angle = characterLocation.getDirection().angle(entityLocation.getDirection());
		if (angle < SIGNIFICANT_ANGLE_OFFSET) {
			if (characterLocation.getWorld() == entityLocation.getWorld()) {
				double distanceSquared = characterLocation.distanceSquared(entityLocation);
				if (distanceSquared < SIGNIFICANT_POSITION_OFFSET_SQUARED) {
					return;
				}
			}
		}

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

	/**
	 * Modes by which to synchronize character and entity location.
	 */
	public static enum Mode {
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
