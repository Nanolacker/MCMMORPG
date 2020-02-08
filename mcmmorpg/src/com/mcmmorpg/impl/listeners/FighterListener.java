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
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.utils.Debug;

public class FighterListener implements Listener {

	private static final Noise BASH_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);

	private PlayerClass fighter;
	private Skill bash;

	public FighterListener() {
		fighter = PlayerClass.forName("Fighter");
		bash = fighter.skillForName("Bash");
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		Skill skill = event.getSkill();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (skill == bash) {
			useBash(pc);
		}
	}

	private void useBash(PlayerCharacter pc) {
		Location location = pc.getLocation();
		Vector lookDirection = location.getDirection();
		World world = location.getWorld();
		location.add(lookDirection).add(0, 1, 0);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
		BASH_NOISE.play(location);
		Collider hitbox = new Collider(location, 1.5, 1.5, 1.5) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(5, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
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
	private void onUseSword(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		if (weapon.getID() != 0) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		Location center = pc.getLocation();
		center.add(0, 1.5, 0).add(center.getDirection().multiply(1.5));
		Collider hitbox = new Collider(center, 1.75, 1.75, 1.75) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) other).getCharacter();
					character.damage(0.25 + 0.1 * pc.getLevel(), pc);
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

}
