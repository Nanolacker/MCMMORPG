package com.mcmmorpg.impl.npcs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;

public class BulskanUndead extends NonPlayerCharacter {

	private final Location spawnLocation;
	private CharacterCollider hitbox;
	private MovementSyncer movementSyncer;
	private Zombie zombie;

	public BulskanUndead(int level, Location location) {
		super("Undead", level, location, maxHealth(level));
		this.spawnLocation = location;
		this.hitbox = new CharacterCollider(this, spawnLocation, 1, 2, 1);
		this.movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
		zombie.setInvulnerable(true);
		zombie.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(zombie);
		movementSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		zombie.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Bukkit.getWorld("world").createExplosion(getLocation(), 10);
		DelayedTask respawn = new DelayedTask(10) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

}
