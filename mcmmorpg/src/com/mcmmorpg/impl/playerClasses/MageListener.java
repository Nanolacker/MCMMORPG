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
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;

public class MageListener implements Listener {

	private static final Noise FIREBALL_CONJURE = new Noise(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
	private static final Noise FIREBALL_HIT_1 = new Noise(Sound.ENTITY_GENERIC_EXPLODE);
	private static final Noise FIREBALL_HIT_2 = new Noise(Sound.BLOCK_FIRE_AMBIENT);
	private static final Noise WHIRLWIND_SPEED_NOISE = new Noise(Sound.ENTITY_WITHER_SHOOT, 1, 2);
	private static final Noise WHIRLWIND_DAMAGE_NOISE = new Noise(Sound.ENTITY_PLAYER_HURT);

	private final PlayerClass mage;
	private final Skill fireball;
	private final Skill iceBeam;
	private final Skill whirlwind;
	private final Skill restore;
	private final Skill earthquake;
	private final Skill darkPulse;

	public MageListener() {
		mage = PlayerClass.forName("Mage");
		fireball = mage.skillForName("Fireball");
		iceBeam = mage.skillForName("Ice Beam");
		whirlwind = mage.skillForName("Whirlwind");
		restore = mage.skillForName("Restore");
		earthquake = mage.skillForName("Earthquake");
		darkPulse = mage.skillForName("Dark Pulse");
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
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
		} else if (skill == darkPulse) {
			useDarkPulse(pc);
		}
	}

	private void useFireball(PlayerCharacter pc) {
		Location start = pc.getLocation();
		Vector lookDirection = start.getDirection();
		start.add(lookDirection).add(0, 1, 0);
		FIREBALL_CONJURE.play(start);
		Vector velocity = lookDirection.multiply(8);
		// ensure we don't shoot through walls
		Location end = pc.getTargetLocation(15);
		double maxDistance = start.distance(end);
		double hitSize = 0.5;
		World world = start.getWorld();
		Fireball fireball = (Fireball) world.spawnEntity(start, EntityType.FIREBALL);
		Projectile projectile = new Projectile(start, velocity, maxDistance, hitSize) {
			@Override
			protected void setLocation(Location location) {
				super.setLocation(location);
			}

			@Override
			protected void onHit(Collider hit) {
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						character.damage(10, pc);
						Location location = getLocation();
						FIREBALL_HIT_1.play(location);
						world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
						for (int i = 0; i < 5; i++) {
							world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
						}
						this.setHitSize(2);
						this.remove();
						new DelayedTask(1) {
							@Override
							protected void run() {
								FIREBALL_HIT_2.play(location);
							}
						}.schedule();
					}
				}
			}

			@Override
			public void remove() {
				super.remove();
				fireball.remove();
			}
		};
		projectile.fire();
	}

	private void useIceBeam(PlayerCharacter pc) {
		double duration = 4;
		double period = 0.2;
		double maxCount = duration / period;
		RepeatingTask channel = new RepeatingTask(period) {
			int count = 0;

			@Override
			protected void run() {
				Location start = pc.getLocation().add(0, 1.25, 0);
				Vector direction = start.getDirection();
				start.add(direction);
				Location end = pc.getTargetLocation(15);
				Ray ray = new Ray(start, end);
				ray.setDrawParticle(Particle.SNOW_SHOVEL);
				ray.draw();
				Raycast raycast = new Raycast(ray, CharacterCollider.class);
				Collider[] hits = raycast.getHits();
				for (Collider hit : hits) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						character.damage(1.5, pc);
						new Noise(Sound.BLOCK_GLASS_BREAK).play(character.getLocation());
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
	}

	private void useWhirlwind(PlayerCharacter pc) {
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
							character.damage(5, pc);
							WHIRLWIND_DAMAGE_NOISE.play(target);
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
		Location center = pc.getLocation();
		double size = 20;
		Collider hitbox = new Collider(center, size, 1, size);
		hitbox.setActive(true);
		int particleCount = 100;
		World world = center.getWorld();
		BlockData particleData = Material.DIRT.createBlockData();
		RepeatingTask update = new RepeatingTask(0.25) {
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
				Collider[] colliders = hitbox.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof CharacterCollider) {
						AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
						if (!character.isFriendly(pc)) {
							character.damage(2, pc);
						}
					}
				}
				count++;
				if (count == 20) {
					hitbox.setActive(false);
					cancel();
				}
			}
		};
		update.schedule();
	}

	private void useDarkPulse(PlayerCharacter pc) {
		Particle particle = Particle.SPELL_WITCH;
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
			return;
		}
		int level = event.getNewLevel();
		if (level == 1) {
			pc.giveItem(Item.forID(1));
			pc.setMaxHealth(25);
			pc.setCurrentHealth(25);
			pc.setHealthRegenRate(0.2);
			pc.setMaxMana(15);
			pc.setCurrentMana(15);
			pc.setManaRegenRate(1);
			pc.grantXp(Integer.MAX_VALUE);
		}
	}

	@EventHandler
	private void onWeaponUse(PlayerCharacterUseWeaponEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
			return;
		}
		Weapon weapon = event.getWeapon();
		if (weapon == null) {
			useFists(pc);
		} else if (weapon.getID() == 1) {
			useFists(pc);
		}
	}

	private void useFists(PlayerCharacter pc) {
		double damage = 1;
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Ray ray = new Ray(start, direction, 3);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
			}
		}
		pc.disarm(1.25);
	}
}
