package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.impl.constants.Quests;
import com.mcmmorpg.impl.constants.Worlds;

public class MelcherTavernRat extends Rat {

	private static final int LEVEL = 1;
	private static final double MAX_HEALTH = 50;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 5;

	private static final Collider spawnBounds;
	public static boolean shouldSpawn;

	static {
		spawnBounds = new Collider(Worlds.ELADRADOR, -1090, 64, 237, -1069, 68, 248);
		spawnBounds.setActive(true);
		RepeatingTask spawner = new RepeatingTask(1) {
			@Override
			protected void run() {
				// Debug.log(shouldSpawn);
				Collider[] colliders = spawnBounds.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof PlayerCharacterCollider) {
						shouldSpawn = true;
						return;
					}
				}
				shouldSpawn = false;
			}
		};
		spawner.schedule();
	}

	public MelcherTavernRat(Location spawnLocation) {
		super(ChatColor.RED + "Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 15);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.PEST_CONTROL.getObjective(0).addProgress(pc, 1);
		}
	}

	@Override
	protected boolean shouldSpawn() {
		return shouldSpawn;
	}

}
