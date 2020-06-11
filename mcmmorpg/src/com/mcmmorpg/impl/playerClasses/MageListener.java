package com.mcmmorpg.impl.playerClasses;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseSkillEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.impl.constants.PlayerClasses;

/**
 * Class for registering events for the mage player class.
 */
public class MageListener implements Listener {

	private static final double[] MAX_HEALTH = { 20.0, 25.0, 31.0, 38.0, 46.0, 55.0, 65.0, 76.0, 88.0, 101.0, 115.0,
			130.0, 146.0, 163.0, 181.0, 200.0, 220.0, 241.0, 263.0, 286.0 };
	private static final double[] HEALTH_REGEN_RATE = { 1, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2, 2.1, 2.2,
			2.3, 2.4, 2.5, 2.7, 2.8, 2.9, 3 };
	private static final double[] MAX_MANA = { 15.0, 19.0, 23.0, 27.0, 31.0, 35.0, 39.0, 43.0, 47.0, 51.0, 55.0, 59.0,
			63.0, 67.0, 71.0, 75.0, 79.0, 83.0, 87.0, 91.0 };
	private static final double[] MANA_REGEN_RATE = { 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0,
			8.5, 9.0, 9.5, 10.0, 10.5, 11.0, 11.5 };
	public static final double STANDARD_SILENCE_DURATION = 1;

	private static final Noise FIREBALL_CONJURE_NOISE = new Noise(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
	private static final Noise FIREBALL_EXPLODE_1_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE);
	private static final Noise FIREBALL_EXPLODE_2_NOISE = new Noise(Sound.BLOCK_FIRE_AMBIENT);
	private static final Noise ICE_BEAM_CHANNEL_NOISE = new Noise(Sound.ENTITY_GENERIC_DRINK, 0.25f, 2f);
	private static final Noise ICE_BEAM_HIT_NOISE = new Noise(Sound.BLOCK_GLASS_BREAK);
	private static final Noise WHIRLWIND_AMBIENT_NOISE = new Noise(Sound.ENTITY_WITHER_DEATH);
	private static final Noise WHIRLWIND_SPEED_NOISE = new Noise(Sound.ENTITY_WITHER_SHOOT, 1, 2);
	private static final Noise EARTHQUAKE_NOISE = new Noise(Sound.BLOCK_GRAVEL_FALL, 1, 0.5f);
	private static final Noise RESTORE_USE_NOISE = new Noise(Sound.BLOCK_ENCHANTMENT_TABLE_USE);
	private static final Noise RESTORE_HEAL_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise SHADOW_VOID_USE_NOISE = new Noise(Sound.BLOCK_PORTAL_TRIGGER);
	private static final Noise SHADOW_VOID_EXPLODE_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);

	private final Skill fireball;
	private final Skill iceBeam;
	private final Skill whirlwind;
	private final Skill earthquake;
	private final Skill restore;
	private final Skill shadowVoid;

	public MageListener() {
		fireball = PlayerClasses.MAGE.skillForName("Fireball");
		iceBeam = PlayerClasses.MAGE.skillForName("Ice Beam");
		whirlwind = PlayerClasses.MAGE.skillForName("Whirlwind");
		earthquake = PlayerClasses.MAGE.skillForName("Earthquake");
		restore = PlayerClasses.MAGE.skillForName("Restore");
		shadowVoid = PlayerClasses.MAGE.skillForName("Shadow Void");
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.MAGE) {
			return;
		}
		int level = event.getNewLevel();
		double maxHealth = MAX_HEALTH[level - 1];
		double healthRegenRate = HEALTH_REGEN_RATE[level - 1];
		double maxMana = MAX_MANA[level - 1];
		double manaRegenRate = MANA_REGEN_RATE[level - 1];
		pc.setMaxHealth(maxHealth);
		pc.setCurrentHealth(maxHealth);
		pc.setHealthRegenRate(healthRegenRate);
		pc.setMaxMana(maxMana);
		pc.setCurrentMana(maxMana);
		pc.setManaRegenRate(manaRegenRate);
	}

	@EventHandler
	private void onUseSkill(PlayerCharacterUseSkillEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.MAGE) {
			return;
		}
		Skill skill = event.getSkill();
		if (skill == fireball) {
			useFireball(pc);
		} else if (skill == iceBeam) {
			useIceBeam(pc);
		} else if (skill == whirlwind) {
			useWhirlwind(pc);
		} else if (skill == earthquake) {
			useEarthquake(pc);
		} else if (skill == restore) {
			useRestore(pc);
		} else if (skill == shadowVoid) {
			useShadowVoid(pc);
		}
	}

	private void useFireball(PlayerCharacter pc) {
		double damageAmount = 4 * pc.getWeapon().getBaseDamage() * fireball.getUpgradeLevel(pc) + 3 * pc.getLevel();
		double range = 15;
		double hitSize = 0.85;
		Location start = pc.getHandLocation().subtract(0, 1, 0);
		Vector lookDirection = start.getDirection();
		start.add(lookDirection).add(0, 1, 0);
		FIREBALL_CONJURE_NOISE.play(start);
		Vector velocity = lookDirection.multiply(10.5);
		// ensure we don't shoot through walls
		Location end = pc.getTargetLocation(range);
		double maxDistance = start.distance(end);
		World world = start.getWorld();
		Fireball fireball = (Fireball) BukkitUtility.spawnNonpersistentEntity(start, EntityType.FIREBALL);
		Projectile projectile = new Projectile(start, velocity, maxDistance, hitSize) {
			// -40 to account for error
			boolean explode = start.distanceSquared(end) < range * range - 40;
			// used to ensure that the fireball only explodes for the first target hit
			boolean hasHitTarget = false;

			@Override
			protected void onHit(Collider hit) {
				if (hasHitTarget) {
					return;
				}
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						explode = true;
						hasHitTarget = true;
						this.remove();
					}
				}
			}

			@Override
			public void remove() {
				super.remove();
				fireball.remove();
				Location location = getLocation();
				if (explode) {
					FIREBALL_EXPLODE_1_NOISE.play(location);
					world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
					for (int i = 0; i < 5; i++) {
						world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
					}
					new DelayedTask(1) {
						@Override
						protected void run() {
							FIREBALL_EXPLODE_2_NOISE.play(getLocation());
						}
					}.schedule();
					Collider hitbox = new Collider(getLocation(), 2, 2, 2) {
						@Override
						protected void onCollisionEnter(Collider other) {
							if (other instanceof CharacterCollider) {
								AbstractCharacter character = ((CharacterCollider) other).getCharacter();
								if (!character.isFriendly(pc)) {
									character.damage(damageAmount, pc);
								}
							}
						}
					};
					hitbox.setActive(true);
					hitbox.setActive(false);
				}
			}
		};
		projectile.fire();
		pc.silence(STANDARD_SILENCE_DURATION);
	}

	private void useIceBeam(PlayerCharacter pc) {
		double damagePerTick = 0.25 * pc.getWeapon().getBaseDamage() * iceBeam.getUpgradeLevel(pc)
				+ 0.5 * pc.getLevel();
		double duration = 4;
		double period = 0.1;
		double maxCount = duration / period;
		World world = pc.getLocation().getWorld();
		RepeatingTask channel = new RepeatingTask(period) {
			int count = 0;

			@Override
			protected void run() {
				Location targetLocationTemp = pc.getTargetLocation(15);
				boolean hitSurface = pc.getLocation().distanceSquared(targetLocationTemp) < 14 * 14;
				AbstractCharacter targetCharacterTemp = null;
				Location crosshair = pc.getLocation().add(0, 1.5, 0);
				ICE_BEAM_CHANNEL_NOISE.play(crosshair);
				Ray hitDetection = new Ray(crosshair, crosshair.getDirection(), 15);
				Raycast raycast = new Raycast(hitDetection, CharacterCollider.class);
				Collider[] hits = raycast.getHits();
				for (Collider hit : hits) {
					if (hit instanceof CharacterCollider) {
						AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
						if (!character.isFriendly(pc)) {
							Location hitLocation = hit.getCenter();
							if (targetLocationTemp == null) {
								targetLocationTemp = hitLocation;
							} else if (hitLocation.distanceSquared(crosshair) < targetLocationTemp
									.distanceSquared(crosshair)) {
								targetLocationTemp = hitLocation;
								targetCharacterTemp = character;
							}
						}
					}
				}
				final Location targetLocation = targetLocationTemp;
				final AbstractCharacter targetCharacter = targetCharacterTemp;

				Location startLocation = pc.getHandLocation();
				Ray beam = new Ray(startLocation, targetLocation);
				beam.draw(Particle.CRIT_MAGIC, 1);
				if (count % 3 == 0) {
					if (targetCharacter != null) {
						targetCharacter.damage(damagePerTick, pc);
						Location hitLocation = targetCharacter.getLocation().add(0, 1, 0);
						world.spawnParticle(Particle.FIREWORKS_SPARK, hitLocation, 10);
						ICE_BEAM_HIT_NOISE.play(hitLocation);
					} else if (hitSurface) {
						world.spawnParticle(Particle.FIREWORKS_SPARK, targetLocation, 10);
						ICE_BEAM_HIT_NOISE.play(targetLocation);
					}
				}
				count++;
				if (count == maxCount) {
					cancel();
				}
			}
		};
		channel.schedule();
		pc.silence(duration);
		pc.disarm(duration);
	}

	private void useWhirlwind(PlayerCharacter pc) {
		double damagePerTick = 1.25 * pc.getWeapon().getBaseDamage() * whirlwind.getUpgradeLevel(pc) + pc.getLevel();
		Location targetTemp = pc.getTargetLocation(15);
		Location location = pc.getLocation().add(0, 1.5, 0);
		Ray ray = new Ray(location, location.getDirection(), 15);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			if (hit instanceof CharacterCollider) {
				AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
				if (!character.isFriendly(pc)) {
					Location hitLocation = hit.getCenter().subtract(0, 1, 0);
					if (targetTemp == null) {
						targetTemp = hitLocation;
					} else if (hitLocation.distanceSquared(location) < targetTemp.distanceSquared(location)) {
						targetTemp = hitLocation;
					}
				}
			}
		}
		final Location target = targetTemp;
		Collider hitbox = new Collider(targetTemp.clone().add(0, 2, 0), 3, 5, 3) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) other).getCharacter();
					if (character.isFriendly(pc)) {
						if (character instanceof PlayerCharacter) {
							int speedAmplifier = whirlwind.getUpgradeLevel(pc);
							PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 8 * 20, speedAmplifier);
							((PlayerCharacter) pc).getPlayer().addPotionEffect(speed);
							WHIRLWIND_SPEED_NOISE.play(target);
						}
					}
				}
			}
		};
		hitbox.setActive(true);
		WHIRLWIND_AMBIENT_NOISE.play(target);
		RepeatingTask update = new RepeatingTask(0.5) {
			int count = 0;

			@Override
			protected void run() {
				drawWhirlwind(target);
				Collider[] colliders = hitbox.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof CharacterCollider) {
						AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
						if (!character.isFriendly(pc)) {
							character.damage(damagePerTick, pc);
						}
					}
				}
				count++;
				if (count == 10) {
					hitbox.setActive(false);
					cancel();
				}
			}
		};
		update.schedule();
		pc.silence(STANDARD_SILENCE_DURATION);
	}

	private void drawWhirlwind(Location location) {
		double height = 0;
		double radius = 0.5;
		for (double t = 0; t < 50; t += 0.05) {
			double x = radius * Math.cos(t);
			double z = radius * Math.sin(t);
			Location particleLocation = location.clone().add(x, height, z);
			particleLocation.getWorld().spawnParticle(Particle.CLOUD, particleLocation, 0);
			height += 0.005;
			radius += 0.0025;
		}
	}

	private void useEarthquake(PlayerCharacter pc) {
		double damagePerTick = 0.75 * pc.getWeapon().getBaseDamage() * earthquake.getUpgradeLevel(pc) + pc.getLevel();
		double size = 15;
		int particleCount = 100;
		Location center = pc.getLocation();
		Collider hitbox = new Collider(center, size, 1, size);
		hitbox.setActive(true);
		World world = center.getWorld();
		BlockData particleData = Material.DIRT.createBlockData();
		RepeatingTask update = new RepeatingTask(0.1) {
			int count = 0;

			@Override
			protected void run() {
				for (int i = 0; i < particleCount; i++) {
					double offsetX = (0.5 - Math.random()) * size;
					double offsetZ = (0.5 - Math.random()) * size;
					Location particleLocation = center.clone().add(offsetX, 0.1, offsetZ);
					if (i % 10 == 0) {
						world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLocation, 0);
					} else {
						world.spawnParticle(Particle.BLOCK_DUST, particleLocation, 0, particleData);
					}
				}
				if (count % 5 == 0) {
					EARTHQUAKE_NOISE.play(center);
					Collider[] colliders = hitbox.getCollidingColliders();
					for (Collider collider : colliders) {
						if (collider instanceof CharacterCollider) {
							AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
							if (!character.isFriendly(pc)) {
								character.damage(damagePerTick, pc);
							}
						}
					}
				}
				count++;
				if (count == 50) {
					hitbox.setActive(false);
					cancel();
				}
			}
		};
		update.schedule();
		pc.silence(STANDARD_SILENCE_DURATION);
	}

	private void useRestore(PlayerCharacter pc) {
		double healProportion = 0.15 + 0.075 * restore.getUpgradeLevel(pc);
		double size = 4;
		int projectileParticleCount = 10;
		int boxParticleCount = 75;
		Location start = pc.getHandLocation();
		RESTORE_USE_NOISE.play(start);
		Vector velocity = start.getDirection().multiply(4);
		Location end = pc.getTargetLocation(15);
		double maxDistance = start.distance(end);
		World world = start.getWorld();

		Projectile projectile = new Projectile(start, velocity, maxDistance, 1.5) {
			boolean hasHitTarget = false;

			@Override
			protected void setLocation(Location location) {
				super.setLocation(location);
				for (int i = 0; i < projectileParticleCount; i++) {
					double offsetX = (Math.random() - 0.5) * 1.5;
					double offsetY = (Math.random() - 0.5) * 1.5;
					double offsetZ = (Math.random() - 0.5) * 1.5;
					Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);
					world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 0);
				}
			}

			@Override
			protected void onHit(Collider hit) {
				if (hasHitTarget) {
					return;
				}
				hasHitTarget = true;
				if (hit instanceof CharacterCollider) {
					AbstractCharacter hitCharacter = ((CharacterCollider) hit).getCharacter();
					if (hitCharacter != pc && hitCharacter.isFriendly(pc)) {
						RESTORE_HEAL_NOISE.play(hitCharacter.getLocation());
						remove();
					}
				}
			}

			@Override
			public void remove() {
				super.remove();
				Location location = getLocation();
				new Noise(Sound.BLOCK_LAVA_EXTINGUISH).play(location);
				Collider hitbox = new Collider(location.clone().add(0, 1, 0), size, size, size) {
					@Override
					protected void onCollisionEnter(Collider other) {
						if (other instanceof CharacterCollider) {
							AbstractCharacter toHeal = ((CharacterCollider) other).getCharacter();
							if (toHeal.isFriendly(pc)) {
								double healAmount = healProportion * pc.getMaxHealth();
								toHeal.heal(healAmount, pc);
							}
						}
					}
				};
				for (int i = 0; i < boxParticleCount; i++) {
					double offsetX = (Math.random() - 0.5) * size;
					double offsetY = (Math.random() - 0.5) * size;
					double offsetZ = (Math.random() - 0.5) * size;
					Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);
					world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 0);
				}
				hitbox.setActive(true);
				hitbox.setActive(false);
			}
		};
		projectile.fire();
		pc.silence(STANDARD_SILENCE_DURATION);
	}

	private void useShadowVoid(PlayerCharacter pc) {
		double damageAmount = 15 * pc.getWeapon().getBaseDamage() * shadowVoid.getUpgradeLevel(pc) + 8 * pc.getLevel();
		double size = 10;
		SHADOW_VOID_USE_NOISE.play(pc.getLocation());
		Location targetTemp = pc.getTargetLocation(15);
		Location location = pc.getLocation().add(0, 1.5, 0);
		Ray ray = new Ray(location, location.getDirection(), 15);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			if (hit instanceof CharacterCollider) {
				AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
				if (!character.isFriendly(pc)) {
					Location hitLocation = hit.getCenter().subtract(0, 1, 0);
					if (targetTemp == null) {
						targetTemp = hitLocation;
					} else if (hitLocation.distanceSquared(location) < targetTemp.distanceSquared(location)) {
						targetTemp = hitLocation;
					}
				}
			}
		}
		final Location target = targetTemp;
		World world = target.getWorld();
		new RepeatingTask(0.2) {
			int count = 0;
			int max = 19;

			@Override
			protected void run() {
				if (count == max) {
					SHADOW_VOID_EXPLODE_NOISE.play(target);
					Collider hitbox = new Collider(target.clone().add(0, 1, 0), size, 2, size) {
						@Override
						protected void onCollisionEnter(Collider other) {
							if (other instanceof CharacterCollider) {
								AbstractCharacter character = ((CharacterCollider) other).getCharacter();
								if (!character.isFriendly(pc)) {
									character.damage(damageAmount, pc);
								}
							}
						}
					};
					hitbox.setActive(true);
					hitbox.setActive(false);
					cancel();
				}
				int particleCount = count * 25;
				for (int i = 0; i < particleCount; i++) {
					double offsetX = (Math.random() - 0.5) * size;
					double offsetY = (Math.random() - 0.5) * 0.5;
					double offsetZ = (Math.random() - 0.5) * size;
					Location particleLocation = target.clone().add(offsetX, offsetY, offsetZ);
					world.spawnParticle(Particle.SPELL_WITCH, particleLocation, 1);
				}
				count++;
			}
		}.schedule();
		pc.silence(STANDARD_SILENCE_DURATION);
	}

}
