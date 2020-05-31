package com.mcmmorpg.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.utils.MathUtils;

import net.md_5.bungee.api.ChatColor;

public class ItemListener implements Listener {

	private static final Noise SWORD_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise STAFF_HIT_NOISE = new Noise(Sound.BLOCK_WOODEN_TRAPDOOR_OPEN);
	private static final Noise USE_POTION_NOISE = new Noise(Sound.ENTITY_GENERIC_DRINK);
	private static final Noise EAT_NOISE = new Noise(Sound.ENTITY_GENERIC_EAT);

	@EventHandler
	private void onUseWeapon(PlayerCharacterUseWeaponEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		PlayerClass playerClass = pc.getPlayerClass();
		Weapon weapon = event.getWeapon();
		if (playerClass == PlayerClasses.FIGHER) {
			useFighterWeapon(pc, weapon);
		} else if (playerClass == PlayerClasses.MAGE) {
			useMageWeapon(pc, weapon);
		}
	}

	private void useFighterWeapon(PlayerCharacter pc, Weapon weapon) {
		double damage = weapon.getBaseDamage() + 2 * pc.getLevel();
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Location particleLocation = start.clone().add(direction.clone().multiply(2));
		particleLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0);
		Ray ray = new Ray(start, direction, 5);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		boolean missed = true;
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
				SWORD_HIT_NOISE.play(character.getLocation());
				missed = false;
			}
		}
		if (!missed) {
			pc.disarm(0.5);
		}
	}

	private void useMageWeapon(PlayerCharacter pc, Weapon weapon) {
		if (weapon == Items.APPRENTICE_STAFF || weapon == Items.STAFF_OF_THE_MELCHER_GUARD) {
			useMageStaff(pc, weapon);
		} else if (weapon == Items.SKELETAL_WAND) {
			useMageWand(pc, weapon, Particle.SPELL_WITCH);
		} else if (weapon == Items.BRITTLE_WAND) {
			useMageWand(pc, weapon, Particle.CRIT);
		}
	}

	private void useMageStaff(PlayerCharacter pc, Weapon weapon) {
		double damage = weapon.getBaseDamage() + pc.getLevel();
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Location particleLocation = start.clone().add(direction.clone().multiply(2));
		particleLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0);
		Ray ray = new Ray(start, direction, 5);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		boolean missed = true;
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
				STAFF_HIT_NOISE.play(character.getLocation());
				missed = false;
			}
		}
		if (!missed) {
			pc.disarm(0.75);
		}
	}

	private void useMageWand(PlayerCharacter pc, Weapon weapon, Particle particleEffect) {
		double damageAmount = weapon.getBaseDamage() + pc.getLevel();
		double maxDistance = 15;
		Location start = pc.getHandLocation();
		Location end = start.clone().add(start.getDirection().multiply(maxDistance));
		Ray ray = new Ray(start, end);
		ray.draw(particleEffect, 1);
		Raycast raycast = new Raycast(ray);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			if (hit instanceof CharacterCollider) {
				AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
				if (!character.isFriendly(pc)) {
					character.damage(damageAmount, pc);
				}
			}
		}
		pc.disarm(1);
	}

	@EventHandler
	private void onUseConsumable(PlayerCharacterUseConsumableItemEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		ConsumableItem consumable = event.getConsumable();
		if (consumable == Items.POTION_OF_MINOR_HEALING) {
			useHealingPotion(pc, 25);
		} else if (consumable == Items.POTION_OF_LESSER_HEALING) {
			useHealingPotion(pc, 50);
		} else if (consumable == Items.POTION_OF_HEALING) {
			useHealingPotion(pc, 80);
		} else if (consumable == Items.POTION_OF_GREATER_HEALING) {
			useHealingPotion(pc, 150);
		} else if (consumable == Items.MELCHER_MEAD) {
			useMelcherMead(pc);
		} else if (consumable == Items.STALE_BREAD) {
			useStaleBread(pc);
		} else if (consumable == Items.GARLIC_BREAD) {
			useGarlicBread(pc);
		}
	}

	private void useHealingPotion(PlayerCharacter pc, double healAmount) {
		USE_POTION_NOISE.play(pc);
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}

	private void useMelcherMead(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		USE_POTION_NOISE.play(pc);
		PotionEffect drunkness = new PotionEffect(PotionEffectType.CONFUSION, MathUtils.secondsToTicks(15), 1);
		player.addPotionEffect(drunkness);
		pc.sendMessage(ChatColor.GRAY + "You fill dizzy.");
	}

	private void useStaleBread(PlayerCharacter pc) {
		EAT_NOISE.play(pc);
		int healAmount = 10;
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}

	private void useGarlicBread(PlayerCharacter pc) {
		EAT_NOISE.play(pc);
		int healAmount = 15;
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");

	}

}
