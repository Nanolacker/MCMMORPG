package com.mcmmorpg.impl.npcs;

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
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.character.XP;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.MathUtils;

public class RottenDweller extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 30;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT, 1f, 0.5f);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SPIDER_HURT, 1f, 0.5f);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private final BossBar bossBar;
	private final Collider bossBarArea;
	private Spider entity;

	public RottenDweller(Location spawnLocation) {
		super(ChatColor.RED + "The Rotten Dweller", 17, spawnLocation);
		super.setMaxHealth(1000);
		super.setHeight(1.5);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, 1.5, 1, 1.5);
		movementSyncer = new MovementSyncer(this, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);
		bossBarArea = new Collider(spawnLocation, 30, 6, 30) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					Player player = ((PlayerCharacterCollider) other).getCharacter().getPlayer();
					bossBar.addPlayer(player);
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					Player player = ((PlayerCharacterCollider) other).getCharacter().getPlayer();
					bossBar.removePlayer(player);
				}
			}
		};
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (damager == RottenDweller.this.entity) {
					if (damaged instanceof Player) {
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						if (pc == null) {
							return;
						}
						pc.damage(50, RottenDweller.this);
					}
				} else if (damaged == RottenDweller.this.entity) {
					DelayedTask cancelKnockback = new DelayedTask(0.1) {
						@Override
						protected void run() {
							entity.setVelocity(new Vector());
						}
					};
					cancelKnockback.schedule();
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Spider) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SPIDER);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(entity);
		bossBarArea.setActive(true);
		movementSyncer.setEnabled(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		bossBarArea.setActive(false);
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 0.5, 0));
		bossBarArea.setCenter(location.clone().add(0, 2, 0));
		if (isSpawned()) {
			entity.teleport(location);
		}
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtils.clamp(currentHealth / getMaxHealth(), 0, 1);
		bossBar.setProgress(progress);
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
		bossBar.setProgress(1);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		bossBarArea.setActive(false);
		entity.remove();
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

}
