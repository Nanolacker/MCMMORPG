package com.mcmmorpg.impl;

import java.util.List;

import org.bukkit.ChatColor;
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
import com.mcmmorpg.common.physics.RaycastHit;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.util.Debug;
import com.mcmmorpg.common.util.MathUtility;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.PlayerClasses;

/**
 * Registers events for handling the use of items.
 */
public class ItemListener implements Listener {

	private static final Noise SWORD_HIT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);
	private static final Noise STAFF_HIT_NOISE = new Noise(Sound.BLOCK_WOODEN_TRAPDOOR_OPEN);
	private static final Noise WAND_FIRE_NOISE = new Noise(Sound.ENTITY_BLAZE_SHOOT, 0.6f, 1.4f);
	private static final Noise WAND_HIT_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH, 1, 1.25f);
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

	/**
	 * Handles the use of a fighter weapon.
	 */
	private void useFighterWeapon(PlayerCharacter pc, Weapon weapon) {
		double damage = weapon.getBaseDamage() * (1 + 0.2 * pc.getLevel());
		damage = Integer.MAX_VALUE;
		Debug.log("god mode");
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Location particleLocation = start.clone().add(direction.clone().multiply(2));
		particleLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0);
		Ray ray = new Ray(start, direction, 4);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		List<RaycastHit> hits = raycast.getHits();
		boolean missed = true;
		for (RaycastHit hit : hits) {
			Collider collider = hit.getCollider();
			AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
				SWORD_HIT_NOISE.play(character.getLocation());
				missed = false;
			}
		}
		if (!missed) {
			pc.disarm(0.75);
		}
	}

	/**
	 * Handles the use of a mage weapon.
	 */
	private void useMageWeapon(PlayerCharacter pc, Weapon weapon) {
		if (weapon == Items.APPRENTICE_STAFF || weapon == Items.STAFF_OF_THE_MELCHER_GUARD) {
			useMageStaff(pc, weapon);
		} else if (weapon == Items.SKELETAL_WAND) {
			useMageWand(pc, weapon, Particle.SPELL_WITCH);
		} else if (weapon == Items.BRITTLE_WAND) {
			useMageWand(pc, weapon, Particle.CRIT);
		}
	}

	/**
	 * Handles the use of a mage staff weapon.
	 */
	private void useMageStaff(PlayerCharacter pc, Weapon weapon) {
		double damage = weapon.getBaseDamage() * (1 + 0.2 * pc.getLevel());
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Location particleLocation = start.clone().add(direction.clone().multiply(2));
		particleLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0);
		Ray ray = new Ray(start, direction, 4);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		List<RaycastHit> hits = raycast.getHits();
		boolean missed = true;
		for (RaycastHit hit : hits) {
			Collider collider = hit.getCollider();
			AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
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

	/**
	 * Handles the use of a mage wand weapon.
	 */
	private void useMageWand(PlayerCharacter pc, Weapon weapon, Particle particleEffect) {
		double damageAmount = (weapon.getBaseDamage() + pc.getLevel());
		double maxDistance = 15;
		Location crosshair = pc.getLocation().add(0, 1.5, 0);
		Ray hitDetection = new Ray(crosshair, crosshair.getDirection(), 15);
		Raycast raycast = new Raycast(hitDetection);
		List<RaycastHit> hits = raycast.getHits();
		Location start = pc.getHandLocation();
		Location end = start.clone().add(start.getDirection().multiply(maxDistance));
		for (RaycastHit hit : hits) {
			Collider collider = hit.getCollider();
			if (collider instanceof CharacterCollider) {
				AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
				if (!character.isFriendly(pc)) {
					character.damage(damageAmount, pc);
					Location hitLocation = hit.getHitLocation();
					WAND_HIT_NOISE.play(hitLocation);
					end = hitLocation;
					break;
				}
			}
		}
		Ray beam = new Ray(start, end);
		beam.draw(particleEffect, 1);
		WAND_FIRE_NOISE.play(start);
		pc.disarm(0.75);
	}

	/**
	 * Handles the consumption of a consumable item.
	 */
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
		} else if (consumable == Items.BOAR_FLANK) {
			useBoarFlank(pc);
		}
	}

	/**
	 * Handles the consumption of a healing potion, healing the player character for
	 * the specified amount.
	 */
	private void useHealingPotion(PlayerCharacter pc, double healAmount) {
		USE_POTION_NOISE.play(pc);
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}

	/**
	 * Handles the consumption of melcher mead, making the player character drunk.
	 */
	private void useMelcherMead(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		USE_POTION_NOISE.play(pc);
		PotionEffect drunkness = new PotionEffect(PotionEffectType.CONFUSION, MathUtility.secondsToTicks(15), 1);
		player.addPotionEffect(drunkness);
		pc.sendMessage(ChatColor.GRAY + "You feel dizzy.");
	}

	/**
	 * Handles the comsumption of stale bread, healing the player character a small
	 * amount.
	 */
	private void useStaleBread(PlayerCharacter pc) {
		EAT_NOISE.play(pc);
		int healAmount = 10;
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}

	/**
	 * Handles the comsumption of garlic bread, healing the player character a small
	 * amount.
	 */
	private void useGarlicBread(PlayerCharacter pc) {
		EAT_NOISE.play(pc);
		int healAmount = 15;
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}

	/**
	 * Handles the comsumption of boar flank, healing the player character a small
	 * amount.
	 */
	private void useBoarFlank(PlayerCharacter pc) {
		EAT_NOISE.play(pc);
		int healAmount = 30;
		pc.heal(healAmount, pc);
		pc.sendMessage(ChatColor.GRAY + "Recovered " + ChatColor.RED + (int) healAmount + " HP");
	}
}
