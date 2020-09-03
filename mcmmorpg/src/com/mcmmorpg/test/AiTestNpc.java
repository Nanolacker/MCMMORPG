package com.mcmmorpg.test;

import org.bukkit.Location;
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
		new RepeatingTask(0.05) {
			@Override
			protected void run() {
				navigator.update();
			}
		}.schedule();
		new RepeatingTask(0.5) {
			@Override
			protected void run() {
				navigator.setDestination(Debug.getAPlayer().getLocation());
			}
		}.schedule();
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
