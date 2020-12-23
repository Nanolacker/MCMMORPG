package com.mcmmorpg.common.ai;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.Character;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;

public class CharacterPathFollower {

	private static final double UPDATE_PERIOD = 0.05;

	private final Character character;
	private final RepeatingTask updateTask;
	private Path path;
	private double speed = 4.0;
	private double stoppingDistanceSquared = 0.05;

	public CharacterPathFollower(Character character) {
		this.character = character;
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
		if (path != null) {
			Debug.drawPath(path, Particle.CRIT, 1);
		}
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
		if (currentLocation.distanceSquared(path.getDestination()) <= stoppingDistanceSquared) {
			updateTask.cancel();
			return;
		}

		Location nextWaypointLocation = path.getWaypoints()[0];
		if (currentLocation.distanceSquared(nextWaypointLocation) <= stoppingDistanceSquared) {
			path = path.getSubpath();
			if (path.isEmpty()) {
				updateTask.cancel();
				return;
			}
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
