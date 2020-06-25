package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.impl.constants.Quests;

public class ForestSpider extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final int SPEED = 1;
	private static final double MAX_HEALTH = 150;
	private static final double DAMAGE_AMOUNT = 5;
	private static final int XP_REWARD = 15;

	public ForestSpider(Location spawnLocation) {
		super(ChatColor.RED + "Forest Spider", LEVEL, spawnLocation, EntityType.CAVE_SPIDER, SPEED, 1.5, 0.75, 1.5,
				XP_REWARD);
		super.setHeight(1);
	}

	@Override
	protected double maxHealth() {
		return MAX_HEALTH;
	}

	@Override
	protected double damageAmount() {
		return DAMAGE_AMOUNT;
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.ARACHNOPHOBIA.getObjective(0).addProgress(pc, 1);
			if (Quests.ARACHNOPHOBIA.getObjective(0).isComplete(pc)
					&& Quests.ARACHNOPHOBIA.getObjective(1).isComplete(pc)) {
				Quests.ARACHNOPHOBIA.getObjective(2).setAccessible(pc, true);
			}
		}
	}
}
