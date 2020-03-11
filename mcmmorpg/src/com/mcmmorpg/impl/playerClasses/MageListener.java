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
import com.mcmmorpg.impl.ItemManager;

public class MageListener implements Listener {

	private static final Noise FIREBALL_CONJURE = new Noise(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
	private static final Noise FIREBALL_EXPLODE_1_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE);
	private static final Noise FIREBALL_EXPLODE_2_NOISE = new Noise(Sound.BLOCK_FIRE_AMBIENT);
	private static final Noise WHIRLWIND_AMBIENT_NOISE = new Noise(Sound.ENTITY_WITHER_DEATH);
	private static final Noise WHIRLWIND_SPEED_NOISE = new Noise(Sound.ENTITY_WITHER_SHOOT, 1, 2);
	private static final Noise EARTHQUAKE_NOISE = new Noise(Sound.BLOCK_GRAVEL_FALL, 1, 0.5f);
	private static final Noise SHADOW_VOID_USE_NOISE = new Noise(Sound.BLOCK_PORTAL_TRIGGER);
	private static final Noise SHADOW_VOID_EXPLODE_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);

	private final PlayerClass mage;
	private final Skill fireball;
	private final Skill iceBeam;
	private final Skill whirlwind;
	private final Skill restore;
	private final Skill earthquake;
	private final Skill shadowVoid;

	public MageListener() {
		mage = PlayerClass.forName("Mage");
		fireball = mage.skillForName("Fireball");
		iceBeam = mage.skillForName("Ice Beam");
		whirlwind = mage.skillForName("Whirlwind");
		restore = mage.skillForName("Restore");
		earthquake = mage.skillForName("Earthquake");
		shadowVoid = mage.skillForName("Shadow Void");
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
			return;
		}
		int level = event.getNewLevel();
		if (level == 1) {
			pc.setMaxHealth(25);
			pc.setCurrentHealth(25);
			pc.setHealthRegenRate(0.2);
			pc.setMaxMana(15);
			pc.setCurrentMana(15);
			pc.setManaRegenRate(1);
			pc.giveItem(Item.forID(10));
		}
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
		} else if (skill == restore) {
			useRestore(pc);
		} else if (skill == shadowVoid) {
			useShadowVoid(pc);
		}
	}

	private void useFireball(PlayerCharacter pc) {
		double damageAmount = 10 * fireball.getUpgradeLevel(pc);
		double range = 15;
		Location start = pc.getLocation();
		Vector lookDirection = start.getDirection();
		start.add(lookDirection).add(0, 1, 0);
		FIREBALL_CONJURE.play(start);
		Vector velocity = lookDirection.multiply(8);
		// ensure we don't shoot through walls
		Location end = pc.getTargetLocation(range);
		double maxDistance = start.distance(end);
		double hitSize = 0.75;
		World world = start.getWorld();
		Fireball fireball = (Fireball) world.spawnEntity(start, EntityType.FIREBALL);
		Projectile projectile = new Projectile(start, velocity, maxDistance, hitSize) {
			// -40 to account for error
			boolean explode = start.distanceSquared(end) < range * range - 40;

			@Override
			protected void setLocation(Location location) {
				super.setLocation(location);
			}

			@Override
			protected void onHit(Collider hit) {
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						explode = true;
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
	}

	private void useIceBeam(PlayerCharacter pc) {
		double duration = 4;
		double period = 0.1;
		double maxCount = duration / period;
		World world = pc.getLocation().getWorld();
		RepeatingTask channel = new RepeatingTask(period) {
			int count = 0;

			@Override
			protected void run() {
				Location targetLocationTemp = pc.getTargetLocation(15);
				AbstractCharacter targetCharacterTemp = null;
				Location startLocation = pc.getLocation().add(0, 1.25, 0);
				Ray ray = new Ray(startLocation, startLocation.getDirection(), 15);
				Raycast raycast = new Raycast(ray, CharacterCollider.class);
				Collider[] hits = raycast.getHits();
				for (Collider hit : hits) {
					if (hit instanceof CharacterCollider) {
						AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
						if (!character.isFriendly(pc)) {
							Location hitLocation = hit.getCenter();
							if (targetLocationTemp == null) {
								targetLocationTemp = hitLocation;
							} else if (hitLocation.distanceSquared(startLocation) < targetLocationTemp
									.distanceSquared(startLocation)) {
								targetLocationTemp = hitLocation;
								targetCharacterTemp = character;
							}
						}
					}
				}
				final Location targetLocation = targetLocationTemp;
				final AbstractCharacter targetCharacter = targetCharacterTemp;

				Ray beam = new Ray(startLocation, targetLocation);
				beam.draw(Particle.CRIT_MAGIC, 2);
				if (count % 5 == 0 && targetCharacter != null) {
					targetCharacter.damage(1.5, pc);
					Location hitLocation = targetCharacter.getLocation();
					world.spawnParticle(Particle.SNOW_SHOVEL, hitLocation, 6);
					new Noise(Sound.BLOCK_GLASS_BREAK).play(hitLocation);
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
							character.damage(5, pc);
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
		double size = 15;
		Collider hitbox = new Collider(center, size, 1, size);
		hitbox.setActive(true);
		int particleCount = 100;
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
								character.damage(2, pc);
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
	}

	private void useRestore(PlayerCharacter pc) {
		double size = 4;
		double healAmount = restore.getUpgradeLevel(pc) * 10;
		Location start = pc.getLocation().add(0, 1.25, 0);
		Vector velocity = start.getDirection().multiply(3);
		Projectile projectile = new Projectile(start, velocity, 15, 1.5) {
			@Override
			protected void setLocation(Location location) {
				super.setLocation(location);
			}

			@Override
			protected void onHit(Collider hit) {
				if (hit instanceof CharacterCollider) {
					AbstractCharacter hitCharacter = ((CharacterCollider) hit).getCharacter();
					if (hitCharacter != pc && hitCharacter.isFriendly(pc)) {
						Collider hitbox = new Collider(hitCharacter.getLocation().add(0, 1, 2), size, size, size) {
							@Override
							protected void onCollisionEnter(Collider other) {
								AbstractCharacter toHeal = ((CharacterCollider) hit).getCharacter();
								if (toHeal.isFriendly(pc)) {
									toHeal.heal(healAmount, pc);
								}
							}
						};
						hitbox.setDrawingEnabled(true);
						hitbox.setActive(true);
						hitbox.setActive(false);
						remove();
					}
				}
			}

			@Override
			public void remove() {
				super.remove();
			}
		};
		projectile.setDrawingEnabled(true);
		projectile.fire();

	}

	private void useShadowVoid(PlayerCharacter pc) {
		double size = 10;
		double damageAmount = 10 * shadowVoid.getUpgradeLevel(pc);
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
	}

	@EventHandler
	private void onWeaponUse(PlayerCharacterUseWeaponEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
			return;
		}
		Weapon weapon = event.getWeapon();
		if (weapon == ItemManager.APPRENTICE_STAFF) {
			useApprenticeStaff(pc);
		}
	}

	private void useApprenticeStaff(PlayerCharacter pc) {
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
