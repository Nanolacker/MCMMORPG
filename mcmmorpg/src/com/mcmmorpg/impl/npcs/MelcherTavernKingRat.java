package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.impl.Quests;

public class MelcherTavernKingRat extends Rat {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 50;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 15;

	public MelcherTavernKingRat(Location spawnLocation) {
		super(ChatColor.RED + "King Rat", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.PEST_CONTROL.getObjective(1).addProgress(pc, 1);
		}
	}

	@Override
	protected boolean shouldSpawn() {
		return MelcherTavernRat.shouldSpawn;
	}

}
