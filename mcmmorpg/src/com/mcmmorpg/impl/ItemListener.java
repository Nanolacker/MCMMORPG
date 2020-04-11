package com.mcmmorpg.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.sound.Noise;

public class ItemListener implements Listener {

	private static final Noise STAFF_HIT_NOISE = new Noise(Sound.BLOCK_WOODEN_TRAPDOOR_OPEN);

	@EventHandler
	private void onUseWeapon(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != PlayerClasses.FIGHER) {
			return;
		}
		if (weapon == Items.APPRENTICE_SWORD) {
			useApprenticeSword(pc);
		} else if (weapon == Items.APPRENTICE_STAFF) {
			useApprenticeStaff(pc);
		}
	}

	private void useFighterWeapon(PlayerCharacter pc, double damage) {
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

	private void useMageStaff(PlayerCharacter pc, double damage) {
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
				STAFF_HIT_NOISE.play(character.getLocation());
			}
		}
		pc.disarm(1.25);
	}

	private void useApprenticeSword(PlayerCharacter pc) {
		useFighterWeapon(pc, 5);
	}

	private void useApprenticeStaff(PlayerCharacter pc) {
		useMageStaff(pc, 4);
	}

}
