package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.constants.Maps;
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
		spawnBounds = new Collider(Worlds.ELADRADOR, -1090, 64, 236, -1070, 69, 248) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.getMap().setMapSegment(Maps.MELCHER_TAVERN_BASEMENT);
					Quests.PEST_CONTROL.getObjective(0).complete(pc);
					Quests.PEST_CONTROL.getObjective(1).setAccessible(pc, true);
					Quests.PEST_CONTROL.getObjective(2).setAccessible(pc, true);
					shouldSpawn = true;
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.getMap().setMapSegment(Maps.ELADRADOR);
					Collider[] collidingColliders = getCollidingColliders();
					for (Collider collider : collidingColliders) {
						if (collider instanceof PlayerCharacterCollider) {
							return;
						}
					}
					shouldSpawn = false;
				}
			}
		};
		spawnBounds.setActive(true);
	}

	public MelcherTavernRat(Location spawnLocation) {
		super(ChatColor.RED + "Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 15);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.PEST_CONTROL.getObjective(1).addProgress(pc, 1);
			if (Quests.PEST_CONTROL.getObjective(1).isComplete(pc)
					&& Quests.PEST_CONTROL.getObjective(2).isComplete(pc)) {
				Quests.PEST_CONTROL.getObjective(3).setAccessible(pc, true);
			}
		}
	}

	@Override
	protected boolean shouldSpawn() {
		return shouldSpawn;
	}

}
