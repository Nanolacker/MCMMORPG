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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.HumanEntity;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.character.XP;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public abstract class AbstractHumanEnemy extends NonPlayerCharacter {

	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);

	protected static final Map<Zombie, AbstractHumanEnemy> aiMap = new HashMap<>();

	protected final HumanEntity entity;
	protected final Location spawnLocation;
	protected final double respawnTime;
	protected final CharacterCollider hitbox;
	protected final MovementSyncer aiSyncer;
	protected Zombie ai;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (aiMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						AbstractHumanEnemy cultist = aiMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(cultist.damageAmount(), cultist);
						cultist.entity.swingHand();
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

	protected AbstractHumanEnemy(String name, int level, Location spawnLocation, double respawnTime, String textureData,
			String textureSignature) {
		super(name, level, spawnLocation);
		this.entity = new HumanEntity(spawnLocation, textureData, textureSignature);
		this.spawnLocation = spawnLocation;
		this.respawnTime = respawnTime;
		this.hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 1, 0), 1, 2, 1);
		this.aiSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		super.setMaxHealth(maxHealth());
	}

	protected abstract double maxHealth();

	protected abstract double damageAmount();

	protected abstract int xpToGrantOnDeath();

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity.setVisible(true);
		ai = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
		ai.setBaby(true);
		ai.setSilent(true);
		ai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
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
		setLocation(spawnLocation);
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
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		XP.distributeXP(getLocation(), 10, xpToGrantOnDeath());
		hitbox.setActive(false);
		entity.setVisible(false);
		ai.remove();
		aiMap.remove(ai);
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		DelayedTask respawn = new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

}
