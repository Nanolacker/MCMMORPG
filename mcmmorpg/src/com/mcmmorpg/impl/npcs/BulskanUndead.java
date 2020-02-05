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
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.Debug;

public class BulskanUndead extends NonPlayerCharacter {

	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_ZOMBIE_DEATH);
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_HURT);

	private final Location spawnLocation;
	private CharacterCollider hitbox;
	private MovementSyncer movementSyncer;
	private Zombie zombie;
	private double respawnTime;

	public BulskanUndead(int level, Location location, double respawnTime) {
		super("Undead", level, location);
		super.setMaxHealth(maxHealth(level));
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
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		zombie.damage(0);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXp();
		zombie.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		DelayedTask respawn = new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				setAlive(true);
				Debug.log("undead respawning");
			}
		};
		respawn.schedule();
	}

	private void grantXp() {
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.grantXp(getGrantedXp());
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
	}

	private int getGrantedXp() {
		return 5 + getLevel() * 2;
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

}
