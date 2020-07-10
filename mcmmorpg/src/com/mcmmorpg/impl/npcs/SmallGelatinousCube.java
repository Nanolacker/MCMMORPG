package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.ai.MotionSynchronizer;
import com.mcmmorpg.common.ai.MotionSynchronizer.MotionSynchronizerMode;
import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.locations.FlintonSewersListener;

public class SmallGelatinousCube extends NonPlayerCharacter {

	private static final int LEVEL = 8;
	private static final int MAX_HEALTH = 75;
	private static final int XP_REWARD = 10;
	private static final double BASIC_ATTACK_DAMAGE = 5;
	private static final int ENTITY_SIZE = 2;
	private static final double HEIGHT = 1.2;
	private static final double WIDTH = 1.2;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final double DISARM_DURATION = 1;

	private static final Map<Slime, SmallGelatinousCube> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MotionSynchronizer motionSyncer;
	private final PlayerCharacter cause;
	private Slime entity;
	private boolean canAttack;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (entityMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						SmallGelatinousCube gelatinousCube = entityMap.get(damager);
						if (gelatinousCube.canAttack) {
							Player player = (Player) damaged;
							PlayerCharacter pc = PlayerCharacter.forPlayer(player);
							pc.damage(BASIC_ATTACK_DAMAGE, gelatinousCube);
							gelatinousCube.disarm();
						}
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

	public SmallGelatinousCube(Location spawnLocation, PlayerCharacter cause) {
		super(ChatColor.RED + "Ooze", LEVEL, spawnLocation);
		super.setMaxHealth(MAX_HEALTH);
		super.setHeight(HEIGHT);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, WIDTH, WIDTH, WIDTH);
		motionSyncer = new MotionSynchronizer(this, MotionSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		this.cause = cause;
		int count = FlintonSewersListener.smallGelatinousCubeCounts.get(cause) + 1;
		FlintonSewersListener.smallGelatinousCubeCounts.put(cause, count);
		canAttack = true;
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Slime) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.SLIME);
		entity.setSize(ENTITY_SIZE);
		entity.setRemoveWhenFarAway(false);
		motionSyncer.setEntity(entity);
		motionSyncer.setEnabled(true);
		entityMap.put(entity, this);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		motionSyncer.setEnabled(false);
		entityMap.remove(entity);
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, WIDTH / 2.0, 0));
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	private void disarm() {
		canAttack = false;
		new DelayedTask(DISARM_DURATION) {
			@Override
			protected void run() {
				canAttack = true;
			}
		}.schedule();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Location location = getLocation();
		PlayerCharacter.distributeXp(location, 25, XP_REWARD);
		int sludgeAmount = (int) (Math.random() * 1.25);
		Items.SLUDGE.drop(location, sludgeAmount);
		hitbox.setActive(false);
		motionSyncer.setEnabled(false);
		int count = FlintonSewersListener.smallGelatinousCubeCounts.get(cause) - 1;
		FlintonSewersListener.smallGelatinousCubeCounts.put(cause, count);
		entityMap.remove(entity);
		entity.remove();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
	}

	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter);
	}

}
