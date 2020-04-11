package com.mcmmorpg.common.character;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Synchronizes the movement of a character to an entity.
 */
public class MovementSyncer {

	private static final double SYNC_PERIOD = 0.1;

	private static final Set<MovementSyncer> activeSyncers;

	static {
		activeSyncers = new HashSet<>();
		RepeatingTask syncTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (MovementSyncer syncer : activeSyncers) {
					AbstractCharacter character = syncer.getCharacter();
					Entity entity = syncer.getEntity();
					MovementSyncMode syncMode = syncer.getSyncMode();
					switch (syncMode) {
					case CHARACTER_FOLLOWS_ENTITY:
						Location entityLocation = entity.getLocation();
						character.setLocation(entityLocation);
						break;
					case ENTITY_FOLLOWS_CHARACTER:
						Location characterLocation = character.getLocation();
						entity.teleport(characterLocation);
						break;
					default:
						break;
					}
				}
			}
		};
		syncTask.schedule();
	}

	private AbstractCharacter character;
	private Entity entity;
	private MovementSyncMode syncMode;

	public MovementSyncer(AbstractCharacter character, Entity entity, MovementSyncMode syncMode) {
		this.character = character;
		this.entity = entity;
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
