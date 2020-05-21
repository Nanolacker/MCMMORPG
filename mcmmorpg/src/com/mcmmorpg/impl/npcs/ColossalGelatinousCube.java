package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ProgressBar;
import com.mcmmorpg.common.ui.ProgressBar.ProgressBarColor;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class ColossalGelatinousCube extends NonPlayerCharacter {

	private static final int LEVEL = 12;
	private static final int MAX_HEALTH = 1000;
	private static final int XP = 20;
	private static final double BASIC_ATTACK_DAMAGE = 4;
	private static final double SPLIT_CHANNEL_RATE = 0.25;
	private static final double SPLIT_COOLDOWN = 15;
	private static final double SPLIT_TRIGGER_RADIUS_SQUARED = 400;
	private static final int MAX_PRE_SPLIT_CHILD_COUNT = 2;
	private static final int SIZE = 15;
	private static final double HEIGHT = 11;
	private static final double WIDTH = 7.7;
	private static final int SLOWNESS = 4;
	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise SPLIT_SPRAY_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final Collider surroundings;
	private final BossBar bossBar;
	private final MovementSynchronizer movementSyncer;
	private Slime entity;
	private ProgressBar splitProgressBar;
	private boolean canUseSplit;
	private int childCount;

	public ColossalGelatinousCube(Location spawnLocation) {
		super(ChatColor.RED + "Colossal Gelatinous Cube", LEVEL, spawnLocation);
		super.setMaxHealth(MAX_HEALTH);
		super.setHeight(HEIGHT);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, WIDTH, WIDTH, WIDTH);
		surroundings = new Collider(getLocation(), 40, 10, 40) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					bossBar.addPlayer(pc.getPlayer());
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					bossBar.removePlayer(pc.getPlayer());
				}
			}
		};
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);
		movementSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
		canUseSplit = true;
		childCount = 0;

		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (damager == ColossalGelatinousCube.this.entity) {
					if (damaged instanceof Player) {
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						pc.damage(BASIC_ATTACK_DAMAGE, ColossalGelatinousCube.this);
					}
				} else if (damaged == ColossalGelatinousCube.this.entity) {
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
		RepeatingTask useSplitTask = new RepeatingTask(0.5) {
			@Override
			protected void run() {
				if (isSpawned()) {
					if (entity.isOnGround()) {
						Entity target = entity.getTarget();
						if (target != null) {
							if (canUseSplit && childCount <= MAX_PRE_SPLIT_CHILD_COUNT && target.getLocation()
									.distanceSquared(getLocation()) < SPLIT_TRIGGER_RADIUS_SQUARED) {
								chargeSplit();
							}
						}
					}
				}
			}
		};
		useSplitTask.schedule();
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtils.clamp(currentHealth / getMaxHealth(), 0.0, 1.0);
		bossBar.setProgress(progress);
	}

	@Override
	protected void onLive() {
		super.onLive();
		bossBar.setProgress(1);
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		surroundings.setActive(true);
		entity = (Slime) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SLIME);
		entity.setSize(SIZE);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, SLOWNESS));
		entity.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(entity);
		movementSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		surroundings.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, WIDTH / 2.0, 0));
		surroundings.setCenter(location);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		entity.damage(0);
		HURT_NOISE.play(getLocation());
	}

	private void chargeSplit() {
		canUseSplit = false;
		PotionEffect immobilizeEffect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128);
		entity.addPotionEffect(immobilizeEffect);
		splitProgressBar = new ProgressBar("Split", ProgressBarColor.WHITE) {
			@Override
			protected void onComplete() {
				if (entity != null) {
					useSplit();
				}
			}
		};
		splitProgressBar.setRate(SPLIT_CHANNEL_RATE);
		List<Player> players = bossBar.getPlayers();
		for (Player player : players) {
			splitProgressBar.display(player);
		}
		DelayedTask splitCooldownTask = new DelayedTask(SPLIT_COOLDOWN) {
			@Override
			protected void run() {
				canUseSplit = true;
			}
		};
		splitCooldownTask.schedule();
	}

	private void useSplit() {
		Location location = getLocation();
		Location[] childLocations = new Location[4];
		childLocations[0] = location.clone().add(WIDTH / 2, 0, 0);
		childLocations[1] = location.clone().add(-WIDTH / 2, 0, 0);
		childLocations[2] = location.clone().add(0, 0, WIDTH / 2);
		childLocations[3] = location.clone().add(0, 0, -WIDTH / 2);

		for (Location childLocation : childLocations) {
			GelatinousCube child = new GelatinousCube(childLocation) {
				@Override
				protected void onDeath() {
					super.onDeath();
					childCount--;
				}
			};
			child.setRespawning(false);
			child.setAlive(true);
			childCount++;
			SPLIT_SPRAY_NOISE.play(location);
			entity.removePotionEffect(PotionEffectType.SLOW);
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, SLOWNESS));
		}
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		Location location = getLocation();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.SAMPLING_SLUDGE.getObjective(1).addProgress(pc, 1);
		}
		int giantSludgeAmount = nearbyPcs.size();
		Items.GIANT_SLUDGE.drop(location, giantSludgeAmount);
		hitbox.setActive(false);
		surroundings.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
		if (splitProgressBar != null) {
			splitProgressBar.dispose();
		}
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		LootChest.spawnLootChest(location);
		DelayedTask respawnTask = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawnTask.schedule();
	}

	private void grantXpToNearbyPlayers() {
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.giveXp(XP);
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
	}

	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter);
	}
}
