package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.Debug;

public class BulskanUndead extends NonPlayerCharacter {

	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_ZOMBIE_DEATH);

	private final Location spawnLocation;
	private CharacterCollider hitbox;
	private MovementSyncer movementSyncer;
	private Zombie zombie;
	private double respawnTime;

	public BulskanUndead(int level, Location location, double respawnTime) {
		super("Undead", level, location, maxHealth(level));
		this.spawnLocation = location;
		this.hitbox = new CharacterCollider(this, spawnLocation, 1, 2, 1);
		this.movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		this.respawnTime = respawnTime;
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.HUSK);
		zombie.setBaby(false);
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
		zombie.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		DelayedTask respawn = new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				setAlive(true);
				Debug.log("respawning");
			}
		};
		respawn.schedule();
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

}
