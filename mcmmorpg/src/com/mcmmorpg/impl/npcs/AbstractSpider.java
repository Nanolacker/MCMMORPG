package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.BukkitUtils;

public abstract class AbstractSpider extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 60;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT);

	private static final Map<Spider, AbstractSpider> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final EntityType entityType;
	private final int speed;
	private final int xpReward;
	private final CharacterCollider hitbox;
	private final MovementSynchronizer movementSyncer;
	private final Collider surroundings;
	private PlayerCharacter target;
	protected Spider entity;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (entityMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						AbstractSpider spider = entityMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(spider.damageAmount(), spider);
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

	public AbstractSpider(String name, int level, Location spawnLocation, EntityType entityType, int speed,
			double lengthX, double lengthY, double lengthZ, int xpReward) {
		super(name, level, spawnLocation);
		super.setMaxHealth(maxHealth());
		this.spawnLocation = spawnLocation;
		this.entityType = entityType;
		this.speed = speed;
		this.xpReward = xpReward;
		hitbox = new CharacterCollider(this, spawnLocation, lengthX, lengthY, lengthZ);
		movementSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		surroundings = new Collider(spawnLocation, 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					onEnterRange(pc);
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					onExitRange(pc);
				}
			}
		};
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Spider) BukkitUtils.spawnNonpersistentEntity(spawnLocation, entityType);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
		@SuppressWarnings("deprecation")
		Entity passenger = entity.getPassenger();
		if (passenger != null) {
			passenger.remove();
		}
		Entity vehicle = entity.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
		PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed);
		entity.addPotionEffect(speedEffect);
		entityMap.put(entity, this);
		surroundings.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		surroundings.setActive(false);
		entity.remove();
		entityMap.remove(entity);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 0.5, 0));
		surroundings.setCenter(location.clone());
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		entity.damage(0);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onLive() {
		super.onLive();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		surroundings.setActive(false);
		entity.remove();
		entityMap.remove(entity);
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		PlayerCharacter.distributeXp(location, 35, xpReward);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	protected void onEnterRange(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		if (target == null) {
			target = PlayerCharacter.forPlayer(player);
			entity.setTarget(target.getPlayer());
		}
	}

	protected void onExitRange(PlayerCharacter pc) {
		if (pc == target) {
			Collider[] colliders = surroundings.getCollidingColliders();
			for (int i = 0; i < colliders.length; i++) {
				Collider collider = colliders[i];
				if (collider instanceof PlayerCharacterCollider) {
					target = ((PlayerCharacterCollider) collider).getCharacter();
					entity.setTarget(target.getPlayer());
					return;
				}
			}
			target = null;
		}
	}

	protected abstract double maxHealth();

	protected abstract double damageAmount();

}
