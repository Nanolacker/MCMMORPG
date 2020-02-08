package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.ProgressBar;

public class BulskanUndead extends NonPlayerCharacter implements Listener {

	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_ZOMBIE_DEATH);
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_HURT);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private Husk entity;
	private final boolean respawn;
	private final double respawnTime;

	public BulskanUndead(int level, Location location, boolean respawn, double respawnTime) {
		super("Undead", level, location);
		super.setMaxHealth(maxHealth(level));
		this.spawnLocation = location;
		this.hitbox = new CharacterCollider(this, spawnLocation, 1, 2, 1);
		this.movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		this.respawn = respawn;
		this.respawnTime = respawnTime;
		EventManager.registerEvents(this);
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		entity = (Husk) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.HUSK);
		entity.setBaby(false);
		entity.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
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
		entity.damage(0);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		entity.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		if (respawn) {
			DelayedTask respawn = new DelayedTask(respawnTime) {
				@Override
				protected void run() {
					setAlive(true);
				}
			};
			respawn.schedule();
		}
	}

	private void grantXpToNearbyPlayers() {
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.grantXp(getXpToGrant());
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
	}

	private int getXpToGrant() {
		return 5 + getLevel() * 2;
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

	@EventHandler
	private void onHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damager == this.entity) {
			if (damaged instanceof Player) {
				Player player = (Player) damaged;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					return;
				}
				pc.damage(getDamageAmount(), this);
			}
		} else if (damaged == this.entity) {
			DelayedTask cancelKnockback = new DelayedTask(0.1) {
				@Override
				protected void run() {
					entity.setVelocity(new Vector());
				}
			};
			cancelKnockback.schedule();
		}
	}

	private double getDamageAmount() {
		return getLevel() * 2;
	}

}
