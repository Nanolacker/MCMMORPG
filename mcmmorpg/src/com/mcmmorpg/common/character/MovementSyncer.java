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

	private static final List<MovementSyncer> activeSyncers;

	static {
		activeSyncers = new ArrayList<>();
		RepeatingTask syncTask = new RepeatingTask(SYNC_PERIOD) {
			@Override
			protected void run() {
				for (MovementSyncer syncer : activeSyncers) {
					CommonCharacter character = syncer.getCharacter();
					Entity entity = syncer.getEntity();
					MovementSyncMode syncMode = syncer.getSyncMode();
					switch (syncMode) {
					case CHARACTER_FOLLOWS_ENTITY:
						Location entityLocation = entity.getLocation();
						character.setLocation(entityLocation);
						break;
					case ENTITY_FOLLOWS_CHARACTER:
						Location charLocation = character.getLocation();
						character.setLocation(charLocation);
						break;
					default:
						break;
					}
				}
			}
		};
		syncTask.schedule();
	}

	private CommonCharacter character;
	private Entity entity;
	private MovementSyncMode syncMode;

	public MovementSyncer(CommonCharacter character, Entity entity, MovementSyncMode syncMode) {
		this.character = character;
		this.entity = entity;
		this.syncMode = syncMode;
	}

	public CommonCharacter getCharacter() {
		return character;
	}

	public void setCharacter(CommonCharacter character) {
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
