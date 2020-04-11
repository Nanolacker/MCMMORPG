package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

public class CultistMage extends AbstractHumanEnemy {

	protected CultistMage(String name, int level, Location spawnLocation, double respawnTime, String textureData,
			String textureSignature) {
		super(name, level, spawnLocation, respawnTime, textureData, textureSignature);
	}

	@Override
	protected double maxHealth() {
		return 0;
	}

	@Override
	protected double damageAmount() {
		return 0;
	}

	@Override
	protected int xpToGrantOnDeath() {
		return 0;
	}

}
