package com.mcmmorpg.impl.playerClasses;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
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
import com.mcmmorpg.common.item.ItemRarity;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;

public class FighterListener implements Listener {

	private static final Noise BASH_MISS_NOISE = new Noise(Sound.ENTITY_WITHER_SHOOT, 1, 2);
	private static final Noise BASH_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise SELF_HEAL_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise CYCLONE_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise LUNGE_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise INSPIRE_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private final PlayerClass fighter;
	private final Skill bash;
	private final Skill selfHeal;
	private final Skill lunge;
	private final Skill cyclone;
	private final Skill overheadStrike;
	private final Skill inspire;

	public FighterListener() {
		fighter = PlayerClass.forName("Fighter");
		bash = fighter.skillForName("Bash");
		selfHeal = fighter.skillForName("Self Heal");
		lunge = fighter.skillForName("Lunge");
		cyclone = fighter.skillForName("Cyclone");
		overheadStrike = fighter.skillForName("Overhead Strike");
		inspire = fighter.skillForName("Inspire");
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != fighter) {
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
			pc.grantXp(Integer.MAX_VALUE);
			Quest.forName("Tutorial Part 1 (Fighter)").start(pc);
		}
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != fighter) {
			return;
		}
		Skill skill = event.getSkill();
		if (skill == bash) {
			useBash(pc);
		} else if (skill == selfHeal) {
			useSelfHeal(pc);
		} else if (skill == lunge) {
			useLunge(pc);
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
		Quest.forName("Tutorial Part 3 (Fighter)").getObjective(0).addProgress(pc, 1);
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
				Collider hitbox = new Collider(location.clone().add(0, 1, 0), 4, 2, 4) {
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

	private void useLunge(PlayerCharacter pc) {
		double damageAmount = 5 * lunge.getUpgradeLevel(pc);
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
						LUNGE_HIT_NOISE.play(character.getLocation());
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
		if (pc.getPlayerClass() != fighter) {
			return;
		}
		if (weapon == null) {
			useFists(pc);
		} else if (weapon.getID() == 0) {
			useShortSword(pc);
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
		pc.disarm(0.75);
	}

	private void useShortSword(PlayerCharacter pc) {
		double damage = 5;
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
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
		pc.disarm(0.75);
	}

}
