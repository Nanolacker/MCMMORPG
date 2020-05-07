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

public abstract class AbstractSpider extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT, 1f, 0.5f);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT, 1f, 0.5f);

	private static final Map<Entity, AbstractSpider> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
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

	public AbstractSpider(String name, int level, Location spawnLocation) {
		super(name, 17, spawnLocation);
		super.setMaxHealth(maxHealth());
		super.setHeight(1.5);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, 1.5, 1, 1.5);
		movementSyncer = new MovementSyncer(this, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Spider) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SPIDER);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
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
		entityMap.put(entity, this);
	}

	@Override
	protected void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
		entityMap.remove(entity);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 0.5, 0));
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
		entity.remove();
		entityMap.remove(entity);
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		XP.distributeXP(location, 35, 400);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	protected abstract double maxHealth();

	protected abstract double damageAmount();

}
