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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.item.LootChest;
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
	private Spider spider;

	public RottenDweller(Location spawnLocation) {
		super(ChatColor.RED + "The Rotten Dweller", 17, spawnLocation);
		super.setMaxHealth(1000);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation, 1.5, 1, 1.5);
		movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
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
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		spider = (Spider) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SPIDER);
		spider.setSilent(true);
		spider.setRemoveWhenFarAway(false);
		movementSyncer.setEntity(spider);
		bossBarArea.setActive(true);
		movementSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		bossBarArea.setActive(false);
		spider.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 1, 0));
		bossBarArea.setCenter(location.clone().add(0, 2, 0));
		spider.teleport(location);
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
		spider.damage(0);
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
		grantXpToNearbyPlayers();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		bossBarArea.setActive(false);
		spider.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		LootChest.spawnLootChest(location, new ItemStack[0]);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	private void grantXpToNearbyPlayers() {
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.grantXp(400);
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
	}

	@EventHandler
	private void onHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damager == this.spider) {
			if (damaged instanceof Player) {
				Player player = (Player) damaged;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					return;
				}
				pc.damage(50, this);
			}
		} else if (damaged == this.spider) {
			DelayedTask cancelKnockback = new DelayedTask(0.1) {
				@Override
				protected void run() {
					spider.setVelocity(new Vector());
				}
			};
			cancelKnockback.schedule();
		}
	}

}
