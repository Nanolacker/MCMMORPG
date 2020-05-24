package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.BukkitUtils;
import com.mcmmorpg.impl.Items;

public class WildBoar extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 60;
	private static final Noise DAMAGE_NOISE = new Noise(Sound.ENTITY_PIG_HURT, 1, 0.5f);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PIG_DEATH, 1, 0.5f);

	private static final Map<PolarBear, WildBoar> aiMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSynchronizer aiSyncer;
	private Pig entity;
	private PolarBear ai;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (aiMap.containsKey(damager)) {
					WildBoar boar = aiMap.get(damager);
					if (damaged instanceof Player) {
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						if (pc == null) {
							return;
						}
						pc.damage(boar.damageAmount(), boar);
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

	public WildBoar(int level, Location spawnLocation) {
		super(ChatColor.YELLOW + "Wild Boar", level, spawnLocation);
		super.setMaxHealth(maxHealth());
		super.setHeight(1);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 0.5, 0), 2, 1, 2);
		aiSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
	}

	private double maxHealth() {
		return 10 + 2 * getLevel();
	}

	private double damageAmount() {
		return getLevel() * 2;
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Pig) BukkitUtils.spawnNonpersistentEntity(spawnLocation, EntityType.PIG);
		entity.setAdult();
		entity.setInvulnerable(true);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
		ai = (PolarBear) BukkitUtils.spawnNonpersistentEntity(spawnLocation, EntityType.POLAR_BEAR);
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
		hitbox.setActive(false);
		entity.remove();
		ai.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		PlayerCharacter.distributeXp(location, 25, getXpToGrant());
		Items.BOAR_FLANK.drop(location, 1);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	private int getXpToGrant() {
		return 5 + getLevel() * 2;
	}

}
