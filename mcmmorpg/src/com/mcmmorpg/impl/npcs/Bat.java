package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.ai.MovementSynchronizer;
import com.mcmmorpg.common.ai.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.util.BukkitUtility;

public class Bat extends NonPlayerCharacter {

	private static final int LEVEL = 10;
	private static final double MAX_HEALTH = 50;
	private static final double DAMAGE_AMOUNT = 10;
	private static final double RESPAWN_TIME = 60;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_BAT_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_BAT_DEATH);

	private static final Map<Vex, Bat> aiMap = new HashMap<>();

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSynchronizer aiSyncer;
	private org.bukkit.entity.Bat entity;
	private Vex ai;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onHit(EntityDamageByEntityEvent event) {
				Entity damager = event.getDamager();
				Entity damaged = event.getEntity();
				if (aiMap.containsKey(damager)) {
					Bat bat = aiMap.get(damager);
					if (damaged instanceof Player) {
						Player player = (Player) damaged;
						PlayerCharacter pc = PlayerCharacter.forPlayer(player);
						if (pc == null) {
							return;
						}
						pc.damage(DAMAGE_AMOUNT, bat);
					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public Bat(Location spawnLocation) {
		super(ChatColor.RED + "Bat", LEVEL, spawnLocation);
		super.setMaxHealth(MAX_HEALTH);
		super.setHeight(1);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone(), 2, 2, 2);
		aiSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (org.bukkit.entity.Bat) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.BAT);
		entity.setInvulnerable(true);
		ai = (Vex) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.VEX);
		ai.setSilent(true);
		ai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		ai.setRemoveWhenFarAway(false);
		aiSyncer.setEntity(ai);
		aiSyncer.setEnabled(true);
		aiMap.put(ai, this);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		aiSyncer.setEnabled(false);
		entity.remove();
		ai.remove();
		aiMap.remove(ai);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 0.5, 0));
		if (isSpawned()) {
			entity.teleport(location);
		}
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		entity.remove();
		ai.remove();
		aiMap.remove(ai);
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

}
