package com.mcmmorpg.impl.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class FighterListener implements Listener {

	private static final Noise BASH_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);
	private static final Noise SELF_HEAL_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise SWEEP_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE);

	private final PlayerClass fighter;
	private final Skill bash;
	private final Skill selfHeal;
	private final Skill sweep;

	public FighterListener() {
		fighter = PlayerClass.forName("Fighter");
		bash = fighter.skillForName("Bash");
		selfHeal = fighter.skillForName("Self Heal");
		sweep = fighter.skillForName("Sweep");
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
		} else if (skill == sweep) {
			useSweep(pc);
		}
	}

	private void useBash(PlayerCharacter pc) {
		Location location = pc.getLocation();
		Vector lookDirection = location.getDirection();
		World world = location.getWorld();
		location.add(lookDirection).add(0, 1, 0);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
		BASH_NOISE.play(location);
		Collider hitbox = new Collider(location, 2, 2, 2) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(10, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	private void useSelfHeal(PlayerCharacter pc) {
		double healAmount = 10 + selfHeal.getUpgradeLevel(pc) * 10;
		pc.heal(healAmount, pc);
		SELF_HEAL_NOISE.play(pc.getLocation());
	}

	private void useSweep(PlayerCharacter pc) {
		Location location = pc.getLocation();
		createSweepEffect(location);
		new DelayedTask(0.25) {
			@Override
			protected void run() {
				createSweepEffect(location);
			}
		}.schedule();
		new DelayedTask(0.5) {
			@Override
			protected void run() {
				createSweepEffect(location);
			}
		}.schedule();
		SWEEP_NOISE.play(location);
		Collider hitbox = new Collider(location, 8, 8, 8) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(10, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	private void createSweepEffect(Location location) {
		World world = location.getWorld();
		Location effectLocation = location.clone();
		// add some variation
		double xOffset = 12 * (Math.random() - 0.5);
		double yOffset = 12 * (Math.random() - 0.5);
		double zOffset = 12 * (Math.random() - 0.5);
		effectLocation.add(xOffset, yOffset, zOffset);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		int level = event.getNewLevel();
		if (level == 1) {
			Weapon weapon = (Weapon) Item.forID(0);
			pc.getInventory().addItem(weapon.getItemStack());
		}
	}

	@EventHandler
	private void onWeaponUse(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		if (weapon.getID() != 0) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		double damage = pc.getLevel() * 0.25;
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
	}

}
