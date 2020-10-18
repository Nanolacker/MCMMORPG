package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Superclass for delayed tasks and repeating tasks.
 */
public abstract class Task {

	private int bukkitTaskID;
	private boolean scheduled;

	Task() {
		bukkitTaskID = -1;
		scheduled = false;
	}

	/**
	 * Schedule this task to run. Throws an IllegalStateException if this task is
	 * already scheduled.
	 */
	public void schedule() {
		if (scheduled) {
			throw new IllegalStateException("Already scheduled");
		}
		scheduled = true;
		scheduleBukkitTask();
	}

	/**
	 * Returns whether this task is scheduled to run.
	 */
	public boolean isScheduled() {
		return scheduled;
	}

	/**
	 * Stops this task from running, rendering it not scheduled. Throws an
	 * IllegalStateException if this task is not currently scheduled. This task can
	 * be rescheduled after being cancelled.
	 */
	public void cancel() {
		if (!scheduled) {
			throw new IllegalStateException("Cannot cancel a task that isn't scheduled");
		}
		scheduled = false;
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.cancelTask(bukkitTaskID);
	}

	protected void setBukkitTaskID(int bukkitTaskID) {
		this.bukkitTaskID = bukkitTaskID;
	}

	protected abstract void scheduleBukkitTask();

	/**
	 * What will be run when this task executes.
	 */
	protected abstract void run();

}
