package com.mcmmorpg.impl.npcs;

import java.util.HashMap;
import java.util.List;
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

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSynchronizer;
import com.mcmmorpg.common.character.MovementSynchronizer.MovementSynchronizerMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Quests;

public class WildBoar extends NonPlayerCharacter {

	private static final int LEVEL = 10;
	private static final double MAX_HEALTH = 500;
	private static final double DAMAGE_AMOUNT = 15;
	private static final int XP_REWARD = 60;
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
						pc.damage(DAMAGE_AMOUNT, boar);
					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public WildBoar(Location spawnLocation) {
		super(ChatColor.YELLOW + "Wild Boar", LEVEL, spawnLocation);
		super.setMaxHealth(MAX_HEALTH);
		super.setHeight(1.25);
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 0.5, 0), 2, 1, 2);
		aiSyncer = new MovementSynchronizer(this, MovementSynchronizerMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	public void spawn() {
		setLocation(spawnLocation);
		super.spawn();
		hitbox.setActive(true);
		entity = (Pig) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.PIG);
		entity.setAI(false);
		entity.setAdult();
		entity.setInvulnerable(true);
		entity.setSilent(true);
		entity.setRemoveWhenFarAway(false);
		ai = (PolarBear) BukkitUtility.spawnNonpersistentEntity(spawnLocation, EntityType.POLAR_BEAR);
		ai.setAdult();
		ai.setCollidable(false);
		ai.setInvulnerable(true);
		ai.setSilent(true);
		ai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		ai.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
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
		PlayerCharacter.distributeXp(location, 25, XP_REWARD);
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.BOARS_GALORE.getObjective(0).addProgress(pc, 1);
		}
		int boarFlankAmount = (int) (Math.random() * 2);
		int tuskAmount = (int) (Math.random() * 3);
		Items.BOAR_FLANK.drop(location, boarFlankAmount);
		Items.BOAR_TUSK.drop(location, tuskAmount);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

}
