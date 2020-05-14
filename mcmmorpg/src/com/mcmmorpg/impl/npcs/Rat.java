package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
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
import com.mcmmorpg.common.character.Xp;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class Rat extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SILVERFISH_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SILVERFISH_DEATH);

	private static final Map<Silverfish, Rat> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final double damageAmount;
	private final int xpReward;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private Silverfish entity;
	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (entityMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						Rat rat = entityMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(rat.damageAmount, rat);
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

	public Rat(String name, int level, Location spawnLocation, double maxHealth, double damageAmount, int xpReward) {
		super(name, level, spawnLocation);
		super.setHeight(1);
		this.spawnLocation = spawnLocation;
		super.setMaxHealth(maxHealth);
		this.damageAmount = damageAmount;
		this.xpReward = xpReward;
		this.hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 0.25, 0), 1, 0.5, 1);
		this.movementSyncer = new MovementSyncer(this, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.add(0, 0.25, 0));
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Silverfish) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SILVERFISH);
		entity.setSilent(true);
		entity.setInvulnerable(true);
		entity.setRemoveWhenFarAway(false);
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
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
		entityMap.remove(entity);
		Location deathLocation = getLocation();
		Xp.distributeXp(deathLocation, 25, xpReward);
		DEATH_NOISE.play(deathLocation);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

}
