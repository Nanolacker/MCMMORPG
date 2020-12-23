package com.mcmmorpg.common.ai;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;

public class CharacterPathFollower {

	private static final double UPDATE_PERIOD = 0.05;
	private static final double DEFAULT_SPEED = 4.0;
	private static final double DEFAULT_STOPPING_DISTANCE_SQUARED = 0.05;

	private final Character character;
	private final RepeatingTask updateTask;
	private Path path;
	private double speed;
	private double stoppingDistanceSquared;

	public CharacterPathFollower(Character character) {
		this.character = character;
		this.speed = DEFAULT_SPEED;
		this.stoppingDistanceSquared = DEFAULT_STOPPING_DISTANCE_SQUARED;
		updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				update();
			}
		};
	}

	public Character getCharacter() {
		return character;
	}

	public Path getPath() {
		return path;
	}

	public void followPath(Path path) {
		this.path = path;
		if (path == null || path.isEmpty()) {
			if (updateTask.isScheduled()) {
				updateTask.cancel();
			}
		} else {
			if (!updateTask.isScheduled()) {
				updateTask.schedule();
			}
		}
	}

	private void update() {
		Location currentLocation = character.getLocation();
		Location nextWaypointLocation = path.getWaypoints()[0];
		Debug.log((int) currentLocation.distanceSquared(nextWaypointLocation) + "vs. " + stoppingDistanceSquared);
		if (currentLocation.distanceSquared(nextWaypointLocation) <= stoppingDistanceSquared) {
			path = path.getSubpath();
			if (path.isEmpty()) {
				updateTask.cancel();
				return;
			}
		} else {
			System.out.println(path.getWaypointCount());
		}
		Vector velocity = nextWaypointLocation.clone().subtract(currentLocation).toVector().normalize().multiply(speed);
		Location nextLocation = currentLocation.add(velocity.multiply(UPDATE_PERIOD));
		Vector direction = path.getDestination().clone().subtract(currentLocation).toVector().normalize();
		nextLocation.setDirection(direction);
		character.setLocation(nextLocation);
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getStoppingDistance() {
		return Math.sqrt(stoppingDistanceSquared);
	}

	public void setStoppingDistance(double stoppingDistance) {
		stoppingDistanceSquared = stoppingDistance * stoppingDistance;
	}

}
