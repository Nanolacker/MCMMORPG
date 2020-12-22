package com.mcmmorpg.common.ai;

import com.mcmmorpg.common.time.RepeatingTask;

public class CharacterPathFollower {

	private static final double UPDATE_PERIOD = 0.05;

	private final Character character;
	private final RepeatingTask updateTask;
	private Path path;
	private int currentWaypointIndex;
	private double speed;

	public CharacterPathFollower(Character character) {
		this.character = character;
		updateTask = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {

			}
		};
	}

	public boolean isEnabled() {
		return updateTask.isScheduled();
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			if (!updateTask.isScheduled()) {
				updateTask.schedule();
			}
		} else {
			if (updateTask.isScheduled()) {
				updateTask.cancel();
			}
		}
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
		currentWaypointIndex = 0;
	}

}
