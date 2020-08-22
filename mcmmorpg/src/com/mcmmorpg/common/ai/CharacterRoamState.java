package com.mcmmorpg.common.ai;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.time.DelayedTask;

public class CharacterRoamState extends State {

	private static final double EPSILON = 0.01;

	private final Character character;
	private Location center;
	private double radiusSquared;
	private double speed;
	private double maxTargetDistance;
	private double idleDuration;
	private boolean idling;
	private Location targetLocation;
	private Vector velocity;
	private Location subTargetLocation;

	public CharacterRoamState(Character character, double speed, Location center, double radius) {
		this.radiusSquared = radius * radius;
	}

	@Override
	protected void initialize(StateMachine stateMachine) {
		Location currentLocation = character.getLocation();
		do {
			double x = Math.random();
			double z = Math.random();
			targetLocation = currentLocation.add(x * maxTargetDistance, 0, z * maxTargetDistance);
			velocity = new Vector(x, 0, z).normalize().multiply(speed);
		} while (currentLocation.distanceSquared(targetLocation) > radiusSquared);
	}

	@Override
	protected void update(StateMachine stateMachine) {
		if (idling) {
			return;
		}

		Location currentLocation = character.getLocation();
		Vector delta = velocity.clone().multiply(stateMachine.getUpdatePeriod());
		Location newLocation = currentLocation.add(delta);
		character.setLocation(newLocation);

		if (newLocation.distanceSquared(targetLocation) <= EPSILON) {
			idling = true;
			new DelayedTask(idleDuration) {
				@Override
				protected void run() {
					idling = false;
				}
			}.schedule();
		}
	}

	@Override
	protected void terminate(StateMachine stateMachine) {

	}

}
