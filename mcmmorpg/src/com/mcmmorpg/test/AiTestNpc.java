package com.mcmmorpg.test;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import com.mcmmorpg.common.ai.CharacterNavigator;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.common.util.Debug;

public class AiTestNpc extends NonPlayerCharacter {

	private static final double SPEED = 5;

	private Villager entity;

	protected AiTestNpc(Location spawnLocation) {
		super("AI Test", 1, spawnLocation);
		CharacterNavigator navigator = new CharacterNavigator(this, SPEED);
		navigator.setDestination(Constants.TEST_SPAWN_LOCATION);
		new RepeatingTask(0.05) {
			@Override
			protected void run() {
				navigator.update();
			}
		}.schedule();
		Debug.log("drawing");
		Debug.drawPath(navigator.getPath(), Particle.CRIT, 10);
	}

	@Override
	protected void spawn() {
		super.spawn();
		entity = (Villager) BukkitUtility.spawnNonpersistentEntity(getLocation(), EntityType.VILLAGER);
		entity.setAI(false);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		if (isSpawned()) {
			entity.teleport(location);
		}
	}

}
