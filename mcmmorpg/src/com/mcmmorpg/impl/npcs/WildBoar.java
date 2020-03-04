package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
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
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class WildBoar extends NonPlayerCharacter implements Listener {

	private static final double respawnTime = 30;
	private static final Noise DAMAGE_NOISE = new Noise(Sound.ENTITY_PIG_HURT, 1, 0.5f);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PIG_DEATH, 1, 0.5f);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private Pig entity;
	private PolarBear ai;
	private final MovementSyncer aiSyncer;

	public WildBoar(int level, Location spawnLocation) {
		super(ChatColor.RED + "Wild Boar", level, spawnLocation);
		super.setMaxHealth(maxHealth(level));
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 0.5, 0), 2, 1, 2);
		aiSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		EventManager.registerEvents(this);
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		entity = (Pig) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.PIG);
		entity.setAdult();
		entity.setInvulnerable(true);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
		ai = (PolarBear) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.POLAR_BEAR);
		ai.setAdult();
		ai.setCollidable(false);
		ai.setInvulnerable(true);
		ai.setSilent(true);
		ai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		ai.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
		ai.setRemoveWhenFarAway(false);
		aiSyncer.setEntity(ai);
		aiSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		aiSyncer.setEnabled(false);
		entity.remove();
		ai.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 0.5, 0));
		entity.teleport(location);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		entity.damage(0);
		DAMAGE_NOISE.play(getLocation());
		if (source instanceof PlayerCharacter) {
			Player player = ((PlayerCharacter) source).getPlayer();
			ai.setTarget(player);
		}
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		hitbox.setActive(false);
		entity.remove();
		ai.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		LootChest.spawnLootChest(location, new ItemStack[0]);
		DelayedTask respawn = new DelayedTask(respawnTime) {
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

	@EventHandler
	private void onHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damager == this.ai) {
			if (damaged instanceof Player) {
				Player player = (Player) damaged;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					return;
				}
				pc.damage(getDamageAmount(), this);
			}
		} else if (damaged == this.ai) {
			DelayedTask cancelKnockback = new DelayedTask(0.1) {
				@Override
				protected void run() {
					ai.setVelocity(new Vector());
				}
			};
			cancelKnockback.schedule();
		}
	}

	private double getDamageAmount() {
		return getLevel() * 2;
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 1, 0);
	}

}
