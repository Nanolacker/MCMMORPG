package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.character.XP;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public abstract class AbstractUndead extends NonPlayerCharacter {

	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PLAYER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_ZOMBIE_DEATH);

	private static final Map<Entity, AbstractUndead> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private final double respawnTime;
	private final EntityType entityType;
	private Zombie entity;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (entityMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						AbstractUndead undead = entityMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(undead.damageAmount(), undead);
					}
				} else if (entityMap.containsKey(damaged)) {
					DelayedTask cancelKnockback = new DelayedTask(0.1) {
						@Override
						protected void run() {
							damaged.setVelocity(new Vector());
						}
					};
					cancelKnockback.schedule();
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public AbstractUndead(String name, int level, Location spawnLocation, double respawnTime, EntityType entityType) {
		super(name, level, spawnLocation);
		super.setMaxHealth(maxHealth());
		this.spawnLocation = spawnLocation;
		this.hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 1.25, 0), 1, 2.5, 1);
		this.movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		this.respawnTime = respawnTime;
		this.entityType = entityType;
	}

	protected abstract double maxHealth();

	protected abstract double damageAmount();

	protected abstract int xpToGrantOnDeath();

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		entity = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
		entity.setBaby(false);
		entity.getEquipment().clear();
		entity.setRemoveWhenFarAway(false);
		entity.setSilent(true);
		entity.eject();
		Entity vehicle = entity.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
		entityMap.put(entity, this);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
		entityMap.remove(entity);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 1, 0));
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
		hitbox.setActive(false);
		grantXpToNearbyPlayers();
		entity.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		if (respawnTime > 0) {
			DelayedTask respawn = new DelayedTask(respawnTime) {
				@Override
				protected void run() {
					setAlive(true);
				}
			};
			respawn.schedule();
		}
		entityMap.remove(entity);
	}

	private void grantXpToNearbyPlayers() {
		XP.distributeXP(getLocation(), 10, xpToGrantOnDeath());
	}

}