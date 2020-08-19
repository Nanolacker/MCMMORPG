package com.mcmmorpg.test;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.ai.State;
import com.mcmmorpg.common.ai.StateMachine;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.common.util.Debug;

public class AiTestNpc extends NonPlayerCharacter {

	private static final double SPEED = .1;
	private static final double MAX_TARGET_DISTANCE = 25;

	private Villager entity;

	protected AiTestNpc(Location spawnLocation) {
		super("AI Test", 1, spawnLocation);
		StateMachine ai = new StateMachine(0.05);
		State roam = new State() {
			Location targetLocation;

			@Override
			protected void initialize(StateMachine stateMachine) {
				Location currentLocation = getLocation();
				targetLocation = currentLocation.add(Math.random() * MAX_TARGET_DISTANCE - MAX_TARGET_DISTANCE / 2, 0,
						Math.random() * MAX_TARGET_DISTANCE - MAX_TARGET_DISTANCE / 2);
			}

			@Override
			protected void update(StateMachine stateMachine) {
				Location currentLocation = getLocation();
				Vector direction = targetLocation.clone().subtract(currentLocation).toVector().normalize();
				Location newLocation = currentLocation.add(direction.multiply(SPEED));
				newLocation.setDirection(direction);
				setLocation(newLocation);
				if (newLocation.distanceSquared(targetLocation) < 0.1) {
					targetLocation = currentLocation.add(Math.random() * MAX_TARGET_DISTANCE - MAX_TARGET_DISTANCE / 2,
							0, Math.random() * MAX_TARGET_DISTANCE - MAX_TARGET_DISTANCE / 2);
				}
			}

			@Override
			protected void terminate(StateMachine stateMachine) {
				targetLocation = null;
			}
		};
		ai.addState(0, roam);
		ai.start(0);
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
