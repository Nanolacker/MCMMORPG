package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.Tutorial;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.impl.constants.Quests;

public class TrainingDummy extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 10;
	private static final Noise DAMAGE_NOISE = new Noise(Sound.BLOCK_IRON_TRAPDOOR_CLOSE);
	private static final Noise DESTROY_NOISE = new Noise(Sound.BLOCK_FENCE_GATE_CLOSE);

	private final CharacterCollider hitbox;
	private ArmorStand entity;

	public TrainingDummy(Location location) {
		super(ChatColor.RED + "Training Dummy", 1, location);
		setMaxHealth(20);
		hitbox = new CharacterCollider(this, location.clone().add(0, 0.75, 0), 1, 2.5, 1);
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (ArmorStand) BukkitUtility.spawnNonpersistentEntity(location, EntityType.ARMOR_STAND);
		hitbox.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
		hitbox.setActive(false);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		DAMAGE_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Location location = getLocation();
		DESTROY_NOISE.play(location);
		entity.remove();
		hitbox.setActive(false);
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 10);
		for (PlayerCharacter pc : nearbyPcs) {
			pc.giveXp(1);
			if (Quests.TUTORIAL.getObjective(7).isAccessible(pc) && !Quests.TUTORIAL.getObjective(7).isComplete(pc)) {
				Quests.TUTORIAL.getObjective(7).addProgress(pc, 1);
				if (Quests.TUTORIAL.getObjective(7).isComplete(pc)) {
					Quests.TUTORIAL.getObjective(8).setAccessible(pc, true);
					Tutorial.message(pc, "Nice job.", 1);
					Tutorial.message(pc, "Now report to the mayor of Melcher. He surely has work for you to do.", 3);
					Tutorial.message(pc, "Look out for a " + ChatColor.YELLOW + "? " + ChatColor.WHITE + "on your map.",
							7);
				}
			}
		}
		DelayedTask respawnTask = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawnTask.schedule();
	}

}
