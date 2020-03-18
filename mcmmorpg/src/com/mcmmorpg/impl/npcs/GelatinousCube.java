package com.mcmmorpg.impl.npcs;

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
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
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

public class GelatinousCube extends NonPlayerCharacter implements Listener {

	private static final int LEVEL = 12;
	private static final double ACID_SPRAY_COOLDOWN = 15;
	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SLIME_DEATH);
	private static final Noise ACID_SPRAY_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private final Location spawnLocation;
	private CharacterCollider hitbox;
	private Slime slime;
	private final MovementSyncer movementSyncer;
	private final RepeatingTask useAcidSprayTask;
	private ProgressBar acidSprayProgressBar;
	private boolean canUseAcidSpray;

	public GelatinousCube(Location spawnLocation) {
		super(ChatColor.RED + "Gelatinous Cube", LEVEL, spawnLocation);
		super.setMaxHealth(50);
		super.setHeight(4);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, 3.5, 3.5, 3.5);
		movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		useAcidSprayTask = new RepeatingTask(0.5) {
			@Override
			protected void run() {
				if (isSpawned()) {
					if (slime.isOnGround()) {
						Entity target = slime.getTarget();
						if (target != null) {
							if (canUseAcidSpray && target.getLocation().distanceSquared(getLocation()) < 36) {
								acidSpray();
							}
						}
					}
				}
			}
		};
		useAcidSprayTask.schedule();
		canUseAcidSpray = true;
		EventManager.registerEvents(this);
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		slime = (Slime) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SLIME);
		slime.setSize(6);
		slime.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
		slime.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(slime);
		movementSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		slime.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 2, 0));
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		slime.damage(0);
		HURT_NOISE.play(getLocation());
	}

	@EventHandler
	private void onHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damager == this.slime) {
			if (damaged instanceof Player) {
				Player player = (Player) damaged;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					return;
				}
				pc.damage(4, this);
			}
		} else if (damaged == this.slime) {
			DelayedTask cancelKnockback = new DelayedTask(0.1) {
				@Override
				protected void run() {
					slime.setVelocity(new Vector());
				}
			};
			cancelKnockback.schedule();
		}
	}

	private void acidSpray() {
		int damageAmount = 20;
		canUseAcidSpray = false;
		slime.setAI(false);
		acidSprayProgressBar = new ProgressBar(getLocation().add(0, 5, 0), ChatColor.WHITE + "Acid Spray", 16,
				ChatColor.AQUA) {
			@Override
			protected void onComplete() {
				if (slime != null) {
					Location location = getLocation();
					ACID_SPRAY_NOISE.play(location);
					slime.setAI(true);
					Collider acidSprayHitbox = new Collider(location.subtract(0, 3, 0), 12, 2, 12) {
						@Override
						protected void onCollisionEnter(Collider other) {
							if (other instanceof CharacterCollider) {
								AbstractCharacter character = ((CharacterCollider) other).getCharacter();
								if (!GelatinousCube.this.isFriendly(character)) {
									character.damage(damageAmount, GelatinousCube.this);
								}
							}
						}
					};
					acidSprayHitbox.drawFill(Particle.SNEEZE, 0.5);
					acidSprayHitbox.setActive(true);
					acidSprayHitbox.setActive(false);
				}
			}
		};
		acidSprayProgressBar.setRate(0.5);
		DelayedTask acidSprayCooldownTask = new DelayedTask(ACID_SPRAY_COOLDOWN) {
			@Override
			protected void run() {
				canUseAcidSpray = true;
			}
		};
		acidSprayCooldownTask.schedule();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		hitbox.setActive(false);
		slime.remove();
		if (acidSprayProgressBar != null) {
			acidSprayProgressBar.dispose();
		}
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
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

	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter);
	}

}
