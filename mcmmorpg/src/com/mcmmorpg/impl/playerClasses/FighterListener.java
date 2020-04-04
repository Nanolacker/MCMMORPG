package com.mcmmorpg.impl.playerClasses;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
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
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.impl.PlayerClasses;
import com.mcmmorpg.impl.Quests;

public class FighterListener implements Listener {

	private static final double[] MAX_HEALTH = { 25.0, 33.0, 43.0, 55.0, 69.0, 85.0, 103.0, 123.0, 145.0, 169.0, 195.0,
			223.0, 253.0, 285.0, 319.0, 355.0, 393.0, 433.0, 475.0, 519.0 };
	private static final double[] HEALTH_REGEN_RATE = { 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0,
			8.5, 9.0, 9.5, 10.0, 10.5, 11.0, 11.5 };
	private static final double[] MAX_MANA = { 10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0, 32.0,
			34.0, 36.0, 38.0, 40.0, 42.0, 44.0, 46.0, 48.0 };
	private static final double[] MANA_REGEN_RATE = { 2, 2.25, 2.5, 2.75, 3, 3.25, 3.5, 3.75, 4, 4.25, 4.5, 4.75, 5,
			5.25, 5.5, 5.75, 6, 6.25, 6.5, 6.75 };

	private static final Noise BASH_MISS_NOISE = new Noise(Sound.ENTITY_WITHER_SHOOT, 1, 2);
	private static final Noise BASH_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise SELF_HEAL_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise CYCLONE_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise CHARGE_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise INSPIRE_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private final Skill bash;
	private final Skill selfHeal;
	private final Skill charge;
	private final Skill cyclone;
	private final Skill overheadStrike;
	private final Skill inspire;

	public FighterListener() {
		bash = PlayerClasses.FIGHER.skillForName("Bash");
		selfHeal = PlayerClasses.FIGHER.skillForName("Self Heal");
		charge = PlayerClasses.FIGHER.skillForName("Charge");
		cyclone = PlayerClasses.FIGHER.skillForName("Cyclone");
		overheadStrike = PlayerClasses.FIGHER.skillForName("Overhead Strike");
		inspire = PlayerClasses.FIGHER.skillForName("Inspire");
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.FIGHER) {
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
		if (level == 1) {
			Quests.TUTORIAL_PART_1_FIGHTER.start(pc);
		}
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.FIGHER) {
			return;
		}
		Skill skill = event.getSkill();
		if (skill == bash) {
			useBash(pc);
		} else if (skill == selfHeal) {
			useSelfHeal(pc);
		} else if (skill == charge) {
			useCharge(pc);
		} else if (skill == cyclone) {
			useCyclone(pc);
		} else if (skill == overheadStrike) {
			useOverheadStrike(pc);
		} else if (skill == inspire) {
			useInspire(pc);
		}
	}

	private void useBash(PlayerCharacter pc) {
		Location location = pc.getLocation();
		Vector lookDirection = location.getDirection();
		World world = location.getWorld();
		location.add(lookDirection.multiply(2.75)).add(0, 1, 0);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
		boolean[] hit = { false };
		Collider hitbox = new Collider(location, 4, 4, 4) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(10 + bash.getUpgradeLevel(pc) * 10, pc);
						BASH_HIT_NOISE.play(location);
						hit[0] = true;
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
		if (!hit[0]) {
			BASH_MISS_NOISE.play(location);
		}
		Quests.TUTORIAL_PART_3_FIGHTER.getObjective(0).addProgress(pc, 1);
	}

	private void useSelfHeal(PlayerCharacter pc) {
		double healAmount = selfHeal.getUpgradeLevel(pc) * 8;
		pc.heal(healAmount, pc);
		SELF_HEAL_NOISE.play(pc.getLocation());
	}

	private void useCyclone(PlayerCharacter pc) {
		Location location = pc.getLocation();
		new RepeatingTask(0.1) {
			int count = 0;

			@Override
			protected void run() {
				Location loc = pc.getLocation();
				loc.setYaw(loc.getYaw() + 90);
				pc.getPlayer().teleport(loc);
				count++;
				// BASH_MISS_NOISE.play(loc);
				Collider hitbox = new Collider(location, 10, 10, 10) {
					@Override
					protected void onCollisionEnter(Collider other) {
						if (other instanceof CharacterCollider) {
							AbstractCharacter target = ((CharacterCollider) other).getCharacter();
							if (!target.isFriendly(pc)) {
								target.damage(2, pc);
								CYCLONE_HIT_NOISE.play(target.getLocation());
							}
						}
					}
				};
				hitbox.setActive(true);
				hitbox.setActive(false);
				createCycloneEffect(location);
				if (count == 4) {
					cancel();
				}
			}
		}.schedule();
	}

	private void createCycloneEffect(Location location) {
		World world = location.getWorld();
		Location effectLocation = location.clone();
		// add some variation
		double xOffset = 6 * (Math.random() - 0.5);
		double zOffset = 6 * (Math.random() - 0.5);
		effectLocation.add(xOffset, 1, zOffset);
		world.spawnParticle(Particle.EXPLOSION_LARGE, effectLocation, 1);
	}

	private final Set<Player> airbornePlayers = new HashSet<>();

	private void useOverheadStrike(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		Location location = player.getLocation();
		Vector direction = location.getDirection();
		direction.setY(1);
		player.setVelocity(direction);
		new DelayedTask(0.1) {
			@Override
			protected void run() {
				airbornePlayers.add(player);
			}
		}.schedule();
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (airbornePlayers.contains(player)) {
			if (player.isOnGround()) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				Location location = player.getLocation();
				Collider hitbox = new Collider(location.clone().add(0, 1, 0), 6, 4, 6) {
					@Override
					protected void onCollisionEnter(Collider other) {
						if (other instanceof CharacterCollider) {
							AbstractCharacter character = ((CharacterCollider) other).getCharacter();
							if (!character.isFriendly(pc)) {
								character.damage(20, pc);
							}
						}
					}
				};
				hitbox.setActive(true);
				hitbox.setActive(false);
				new Noise(Sound.ENTITY_GENERIC_EXPLODE).play(location);
				location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
				airbornePlayers.remove(player);
			}
		}
	}

	private void useCharge(PlayerCharacter pc) {
		double damageAmount = 5 * charge.getUpgradeLevel(pc);
		Player player = pc.getPlayer();
		Location location = player.getLocation();
		Vector direction = location.getDirection();
		direction.setY(0);
		direction.multiply(1.5);
		player.setVelocity(direction);
		Vector velocity = direction.multiply(10);
		Projectile projectile = new Projectile(location.clone().add(0, 1, 0), velocity, 6, 2) {
			@Override
			protected void onHit(Collider hit) {
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						character.damage(damageAmount, pc);
						CHARGE_HIT_NOISE.play(character.getLocation());
					}
				}
			}
		};
		projectile.fire();
	}

	private void useInspire(PlayerCharacter pc) {
		double healAmount = inspire.getUpgradeLevel(pc) * 5;
		double size = 8;
		Location location = pc.getLocation().add(0, 1, 0);
		Collider hitbox = new Collider(location, size, 2, size) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) other).getCharacter();
					if (character.isFriendly(pc)) {
						character.heal(healAmount, pc);
					}
				}
			}
		};
		int particleCount = 50;
		World world = location.getWorld();
		for (int i = 0; i < particleCount; i++) {
			double xOffset = (Math.random() - 0.5) * size;
			double yOffset = (Math.random() - 0.5) * 1;
			double zOffset = (Math.random() - 0.5) * size;
			Location particleLocation = location.clone().add(xOffset, yOffset, zOffset);
			world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 1);
		}
		INSPIRE_NOISE.play(location);
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	@EventHandler
	private void onUseWeapon(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.FIGHER) {
			return;
		}
		if (weapon == Item.forName("Apprentice Sword")) {
			useApprenticeSword(pc);
		}
	}

	private void useWeapon(PlayerCharacter pc, double damage) {
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Location particleLocation = start.clone().add(direction.clone().multiply(2));
		particleLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0);
		Ray ray = new Ray(start, direction, 3);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
				new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR).play(character.getLocation());
			}
		}
		pc.disarm(0.5);
	}

	private void useApprenticeSword(PlayerCharacter pc) {
		useWeapon(pc, 5);
	}

}
