package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class CommonTask {

	private int bukkitTaskID;
	/**
	 * The time at which this task was scheduled.
	 */
	private double scheduleTimeSeconds;

	public CommonTask() {
		bukkitTaskID = -1;
	}

	public void schedule() {
		scheduleTimeSeconds = GameClock.getTimeSeconds();
		scheduleBukkitTask();
	}

	public void cancel() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.cancelTask(bukkitTaskID);
	}

	protected void setBukkitTaskID(int bukkitTaskID) {
		this.bukkitTaskID = bukkitTaskID;
	}

	protected abstract void scheduleBukkitTask();

	protected abstract void run();

}
