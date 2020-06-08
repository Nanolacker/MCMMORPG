package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.HumanEntity;
import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.util.BukkitUtility;

public abstract class AbstractHumanEnemy extends NonPlayerCharacter {

	protected static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY,
			Integer.MAX_VALUE, 1);

	protected static final Map<Zombie, AbstractHumanEnemy> aiMap = new HashMap<>();

	protected final HumanEntity entity;
	protected final Location spawnLocation;
	private final double damageAmount;
	private final int xpReward;
	protected final double respawnTime;
	private int speed;
	protected final CharacterCollider hitbox;
	protected final MovementSynchronizer aiSyncer;
	protected Zombie ai;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (aiMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						AbstractHumanEnemy human = aiMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(human.damageAmount, human);
						human.entity.swingHand();
					}
				} else if (aiMap.containsKey(damaged)) {
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

	protected AbstractHumanEnemy(String name, int level, Location spawnLocation, double maxHealth, double damageAmount,
			int xpReward, double respawnTime, int speed, String textureData, String textureSignature) {
		super(name, level, spawnLocation);
		this.spawnLocation = spawnLocation;
		super.setMaxHealth(maxHealth);
		this.damageAmount = damageAmount;
		this.xpReward = xpReward;
		this.respawnTime = respawnTime;
		this.speed = speed;
		this.entity = new HumanEntity(spawnLocation, textureData, textureSignature);
		this.hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 1, 0), 1, 2, 1);
		this.aiSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity.setVisible(true);
		ai = (Zombie) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.ZOMBIE);
		ai.setBaby(false);
		ai.setSilent(true);
		ai.addPotionEffect(INVISIBILITY);
		PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed);
		ai.addPotionEffect(speedEffect);
		ai.eject();
		Entity vehicle = ai.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		ai.setRemoveWhenFarAway(false);
		ai.getEquipment().clear();
		aiSyncer.setEntity(ai);
		aiSyncer.setEnabled(true);
		aiMap.put(ai, this);
	}

	@Override
	protected void despawn() {
		super.despawn();
		hitbox.setActive(false);
		aiSyncer.setEnabled(false);
		entity.setVisible(false);
		ai.remove();
		aiMap.remove(ai);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 1, 0));
		entity.setLocation(location);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		entity.hurt();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		PlayerCharacter.distributeXp(getLocation(), 25, xpReward);
		hitbox.setActive(false);
		entity.setVisible(false);
		ai.remove();
		aiMap.remove(ai);
		Location location = getLocation();
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		DelayedTask respawn = new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

}
