package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class CommonTask {

	private int bukkitTaskID;
	private boolean scheduled;

	public CommonTask() {
		bukkitTaskID = -1;
		scheduled = false;
	}

	public void schedule() {
		if (scheduled) {
			throw new IllegalStateException("Already scheduled");
		}
		scheduled = true;
		scheduleBukkitTask();
	}

	public boolean isScheduled() {
		return scheduled;
	}

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

	protected abstract void run();

}
