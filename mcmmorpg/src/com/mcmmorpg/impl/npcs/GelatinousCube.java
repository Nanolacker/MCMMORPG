package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ProgressBar;

public class GelatinousCube extends NonPlayerCharacter implements Listener {

	private final Location spawnLocation;
	private final boolean respawn;
	private CharacterCollider hitbox;
	private Slime slime;
	private final MovementSyncer movementSyncer;
	private RepeatingTask splitTask;
	private boolean hasSplit;

	public GelatinousCube(int level, Location spawnLocation, boolean respawn) {
		super(ChatColor.RED + "Gelatinous Cube", level, spawnLocation);
		setMaxHealth(50);
		this.spawnLocation = spawnLocation;
		this.respawn = respawn;
		hitbox = new CharacterCollider(this, spawnLocation, 3.5, 3.5, 3.5);
		movementSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		splitTask = new RepeatingTask(15) {
			@Override
			protected void run() {
				if (isSpawned()) {
					split();
				}
			}
		};
		splitTask.schedule();
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
	protected Location getNameplateLocation() {
		return getLocation().add(0, 4, 0);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		slime.damage(0);
		// HURT_NOISE.play(getLocation());
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

	private void split() {
		slime.setAI(false);
		ProgressBar progress = new ProgressBar(getLocation().add(0, 5, 0), ChatColor.WHITE + "Split", 16,
				ChatColor.AQUA) {
			@Override
			protected void onComplete() {
				if (slime != null) {
					slime.setAI(true);
					new GelatinousCube(getLevel(), getLocation(), false).setAlive(true);
				}
			}
		};
		progress.setRate(0.25);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		hitbox.setActive(false);
		slime.remove();
		Location location = getLocation();
		// DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		LootChest.spawnLootChest(location);
		if (respawn) {
			DelayedTask respawnTask = new DelayedTask(10) {
				@Override
				protected void run() {
					setAlive(true);
				}
			};
			respawnTask.schedule();
		}
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

}
