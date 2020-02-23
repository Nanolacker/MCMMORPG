package com.mcmmorpg.impl.playerClasses;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.SkillUseEvent;
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

	private final PlayerClass mage;
	private final Skill fireball;
	private final Skill iceBeam;
	private final Skill restore;
	private final Skill earthquake;
	private final Skill darkPulse;

	public MageListener() {
		mage = PlayerClass.forName("Mage");
		fireball = mage.skillForName("Fireball");
		iceBeam = mage.skillForName("Ice Beam");
		restore = mage.skillForName("Restore");
		earthquake = mage.skillForName("Iceball");
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
		Player player = pc.getPlayer();
		Block target = player.getTargetBlock(null, 15);
		// ensure we don't shoot through walls
		double maxDistance = start.distance(target.getLocation());
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
		double range = 15;
		double maxCount = duration / period;
		RepeatingTask channel = new RepeatingTask(period) {
			int count = 0;

			@Override
			protected void run() {
				Location location = pc.getLocation().add(0, 1.25, 0);
				Vector direction = location.getDirection();
				location.add(direction);
				Ray ray = new Ray(location, direction, range);
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

	private void useEarthquake(PlayerCharacter pc) {
		Location center = pc.getLocation();
		double size = 5;
		Collider hitbox = new Collider(center, size, 1, size);
		int particleCount = 20;
		World world = center.getWorld();
		for (int i = 0; i < particleCount; i++) {
			double offsetX = (0.5 - Math.random()) * size;
			double offsetZ = (0.5 - Math.random()) * size;
			Location particleLocation = center.clone().add(offsetX, 0.1, offsetZ);
			world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLocation, 1);
		}
	}

	private void useDarkPulse(PlayerCharacter pc) {
		Particle particle = Particle.SPELL_WITCH;
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		int level = event.getNewLevel();
		if (level == 1) {
			// starting gear
		}
	}

	@EventHandler
	private void onWeaponUse(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
	}

}
