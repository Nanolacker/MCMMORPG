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
	private static final double MAX_HEALTH = 120;
	private static final double DAMAGE_AMOUNT = 7;
	private static final int XP_REWARD = 10;

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
		}
	}
}
