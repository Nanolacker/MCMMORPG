package com.mcmmorpg.common.character;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Synchronizes the movement of a character to an entity.
 */
public class MovementSyncer {

	private static final double SYNC_PERIOD = 0.1;
	private static final double SIGNIFICANT_OFFSET_SQUARED = 0.01;

	private static final List<MovementSyncer> activeSyncers = new ArrayList<>();

	private AbstractCharacter character;
	private Entity entity;
	private MovementSyncMode syncMode;

	static {
		RepeatingTask syncTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < activeSyncers.size(); i++) {
					MovementSyncer syncer = activeSyncers.get(i);
					AbstractCharacter character = syncer.getCharacter();
					Entity entity = syncer.getEntity();
					Location characterLocation = character.getLocation();
					Location entityLocation = entity.getLocation();
					if (characterLocation.getWorld() != entityLocation.getWorld()
							|| entityLocation.distanceSquared(characterLocation) > SIGNIFICANT_OFFSET_SQUARED) {
						MovementSyncMode syncMode = syncer.getSyncMode();
						switch (syncMode) {
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
		syncTask.schedule();
	}

	public MovementSyncer(AbstractCharacter character, MovementSyncMode syncMode) {
		this.character = character;
		this.entity = null;
		this.syncMode = syncMode;
	}

	public AbstractCharacter getCharacter() {
		return character;
	}

	public void setCharacter(AbstractCharacter character) {
		this.character = character;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isEnabled() {
		return activeSyncers.contains(this);
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			activeSyncers.add(this);
		} else {
			activeSyncers.remove(this);
		}
	}

	public void setSyncMode(MovementSyncMode syncMode) {
		this.syncMode = syncMode;
	}

	public MovementSyncMode getSyncMode() {
		return syncMode;
	}

	public static enum MovementSyncMode {
		CHARACTER_FOLLOWS_ENTITY, ENTITY_FOLLOWS_CHARACTER
	}

}
