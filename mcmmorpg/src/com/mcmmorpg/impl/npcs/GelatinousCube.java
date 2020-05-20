package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ProgressBar;
import com.mcmmorpg.common.ui.ProgressBar.ProgressBarColor;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class GelatinousCube extends NonPlayerCharacter {

	private static final int LEVEL = 12;
	private static final int MAX_HEALTH = 150;
	private static final int XP_REWARD = 20;
	private static final double BASIC_ATTACK_DAMAGE = 4;
	private static final double ACID_SPRAY_DAMAGE = 10;
	private static final double ACID_SPRAY_CHANNEL_RATE = 0.35;
	private static final double ACID_SPRAY_COOLDOWN = 15;
	private static final double ACID_SPRAY_TRIGGER_RADIUS_SQUARED = 36;
	private static final double ACID_SPRAY_DAMAGE_WIDTH = 10;
	private static final double ACID_SPRAY_DAMAGE_HEIGHT = 2;
	private static final int SIZE = 5;
	private static final double HEIGHT = 3.5;
	private static final double WIDTH = 2.9;
	private static final int SLOWNESS = 2;
	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise ACID_SPRAY_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private static final Map<Slime, GelatinousCube> entityMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSynchronizer movementSyncer;
	private Slime entity;
	private ProgressBar acidSprayProgressBar;
	private boolean canUseAcidSpray;
	private boolean respawning;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (entityMap.containsKey(damager)) {
					if (damaged instanceof Player) {
						GelatinousCube gelatinousCube = entityMap.get(damager);
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(BASIC_ATTACK_DAMAGE, gelatinousCube);
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
		RepeatingTask useAcidSprayTask = new RepeatingTask(0.5) {
			@Override
			protected void run() {
				Set<Slime> entities = entityMap.keySet();
				for (Slime entity : entities) {
					GelatinousCube gelatinousCube = entityMap.get(entity);
					if (gelatinousCube.isSpawned()) {
						if (entity.isOnGround()) {
							Entity target = entity.getTarget();
							if (target != null) {
								if (gelatinousCube.canUseAcidSpray && target.getLocation().distanceSquared(
										gelatinousCube.getLocation()) < ACID_SPRAY_TRIGGER_RADIUS_SQUARED) {
									gelatinousCube.chargeAcidSpray();
								}
							}
						}
					}
				}
			}
		};
		useAcidSprayTask.schedule();
	}

	public GelatinousCube(Location spawnLocation) {
		super(ChatColor.RED + "Gelatinous Cube", LEVEL, spawnLocation);
		super.setMaxHealth(MAX_HEALTH);
		super.setHeight(HEIGHT);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, WIDTH, WIDTH, WIDTH);
		movementSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		canUseAcidSpray = true;
		respawning = true;
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Slime) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SLIME);
		entity.setSize(SIZE);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, SLOWNESS));
		entity.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
		entityMap.put(entity, this);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entityMap.remove(entity);
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, WIDTH / 2.0, 0));
		if (acidSprayProgressBar != null) {
			acidSprayProgressBar.display(location.clone().add(0, HEIGHT + 1, 0));
		}
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		entity.damage(0);
		HURT_NOISE.play(getLocation());
	}

	private void chargeAcidSpray() {
		canUseAcidSpray = false;
		PotionEffect immobilizeEffect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128);
		entity.addPotionEffect(immobilizeEffect);
		acidSprayProgressBar = new ProgressBar("Acid Spray", ProgressBarColor.WHITE) {
			@Override
			protected void onComplete() {
				if (entity != null) {
					useAcidSpray();
				}
			}
		};
		acidSprayProgressBar.setRate(ACID_SPRAY_CHANNEL_RATE);
		acidSprayProgressBar.display(getLocation().add(0, HEIGHT + 1, 0));
		DelayedTask acidSprayCooldownTask = new DelayedTask(ACID_SPRAY_COOLDOWN) {
			@Override
			protected void run() {
				canUseAcidSpray = true;
			}
		};
		acidSprayCooldownTask.schedule();
	}

	private void useAcidSpray() {
		Location location = getLocation();
		ACID_SPRAY_NOISE.play(location);
		Collider acidSprayHitbox = new Collider(location.add(0, WIDTH / 2, 0), ACID_SPRAY_DAMAGE_WIDTH,
				ACID_SPRAY_DAMAGE_HEIGHT, ACID_SPRAY_DAMAGE_WIDTH) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) other).getCharacter();
					if (!GelatinousCube.this.isFriendly(character)) {
						character.damage(ACID_SPRAY_DAMAGE, GelatinousCube.this);
					}
				}
			}
		};
		acidSprayHitbox.drawFill(Particle.SNEEZE, 0.5);
		acidSprayHitbox.setActive(true);
		acidSprayHitbox.setActive(false);
		entity.removePotionEffect(PotionEffectType.SLOW);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, SLOWNESS));
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Location location = getLocation();
		PlayerCharacter.distributeXp(location, 25, XP_REWARD);
		int sludgeAmount = (int) (Math.random() * 3);
		Items.SLUDGE.drop(location, sludgeAmount);
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.SAMPLING_SLUDGE.getObjective(0).addProgress(pc, 1);
		}
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entityMap.remove(entity);
		entity.remove();
		if (acidSprayProgressBar != null) {
			acidSprayProgressBar.dispose();
			acidSprayProgressBar = null;
		}
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		if (respawning) {
			DelayedTask respawnTask = new DelayedTask(RESPAWN_TIME) {
				@Override
				protected void run() {
					setAlive(true);
				}
			};
			respawnTask.schedule();
		}
	}

	public void setRespawning(boolean respawning) {
		this.respawning = respawning;
	}

	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter);
	}

}
