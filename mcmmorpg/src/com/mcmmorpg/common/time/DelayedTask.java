package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.util.MathUtility;

/**
 * A task that will execute after a specified duration.
 */
public abstract class DelayedTask extends Task {

	private double delay;

	/**
	 * A task that will run after the specified delay.
	 */
	public DelayedTask(double delay) {
		this.delay = delay;
	}

	/**
	 * A task that will run on the next tick.
	 */
	public DelayedTask() {
		this(0.0);
	}

	@Override
	protected final void scheduleBukkitTask() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		Plugin plugin = MMORPGPlugin.getInstance();
		Runnable runnable = () -> run();
		long delayTicks = MathUtility.secondsToTicks(delay);
		int bukkitTaskID = scheduler.scheduleSyncDelayedTask(plugin, runnable, delayTicks);
		setBukkitTaskID(bukkitTaskID);
	}

}
