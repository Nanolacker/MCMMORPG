package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

public class StationaryCultistMage extends CultistMage {

	public StationaryCultistMage(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	protected void spawn() {
		super.spawn();
		ai.addPotionEffect(SLOW_EFFECT);
	}

	@Override
	protected void cancelSpell() {
		super.cancelSpell();
		ai.addPotionEffect(SLOW_EFFECT);
	}

}
