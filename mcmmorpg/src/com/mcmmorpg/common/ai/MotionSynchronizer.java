package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Synchronizes the motion of a character and an entity. A motion
 * synchronizers mode determines its follow behavior.
 */
public class MotionSynchronizer {

	private static final double SYNC_PERIOD = 0.1;
	private static final double SIGNIFICANT_POSITION_OFFSET_SQUARED = 0.01;
	private static final double SIGNIFICANT_ANGLE_OFF_SET = Math.toRadians(5);

	private static final List<MotionSynchronizer> activeSynchronizers = new ArrayList<>();

	private AbstractCharacter character;
	private Entity entity;
	private MotionSynchronizerMode mode;

	static {
		RepeatingTask synchronizerTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < activeSynchronizers.size(); i++) {
					MotionSynchronizer synchronizer = activeSynchronizers.get(i);
					synchronizer.updateLocation();
				}
			}
		};
		synchronizerTask.schedule();
	}

	/**
	 * Creates a new movement synchronizer for the specified character.
	 */
	public MotionSynchronizer(AbstractCharacter character, MotionSynchronizerMode syncMode) {
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
		return activeSynchronizers.contains(this);
	}

	/**
	 * Sets whether this movement synchronizer is active and synchronizing.
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			activeSynchronizers.add(this);
		} else {
			activeSynchronizers.remove(this);
		}
	}

	/**
	 * Sets the mode of this movement synchronizer.
	 */
	public void setMode(MotionSynchronizerMode syncMode) {
		this.mode = syncMode;
	}

	/**
	 * Returns the mode of this movement synchronizer.
	 */
	public MotionSynchronizerMode getMode() {
		return mode;
	}

	private void updateLocation() {
		Location characterLocation = character.getLocation();
		Location entityLocation = entity.getLocation();

		double angle = characterLocation.getDirection().angle(entityLocation.getDirection());
		if (angle < SIGNIFICANT_ANGLE_OFF_SET) {
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
	 * Modes by which to synchronize character and entity motion.
	 */
	public static enum MotionSynchronizerMode {
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
