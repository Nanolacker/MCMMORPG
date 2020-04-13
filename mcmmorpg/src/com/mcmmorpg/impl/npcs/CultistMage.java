package com.mcmmorpg.impl.npcs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.mcmmorpg.common.event.EventManager;

public class CultistMage extends AbstractHumanEnemy {

	private static final Set<Zombie> aiSet = new HashSet<>();

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onTarget(EntityTargetEvent event) {
				Entity targeter = event.getEntity();
				if (aiSet.contains(targeter)) {
					CultistMage mage = (CultistMage) aiMap.get(targeter);
					Entity target = event.getTarget();
					if (target == null) {
						mage.
					} else {

					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

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

	@Override
	protected void spawn() {
		super.spawn();
		aiSet.add(getAI());
	}

	@Override
	protected void despawn() {
		aiSet.remove(getAI());
		super.despawn();
	}

	@Override
	protected void onDeath() {
		aiSet.remove(getAI());
		super.onDeath();
	}

}
