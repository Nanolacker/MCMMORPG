package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;

/**
 * A task that will execute after a specified duration.
 */
public abstract class DelayedTask extends CommonTask {

	private double delaySeconds;

	/**
	 * A task that will run after the specified delay.
	 */
	public DelayedTask(double delaySeconds) {
		this.delaySeconds = delaySeconds;
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
		Plugin plugin = MMORPGPlugin.getPlugin(MMORPGPlugin.class);
		Runnable runnable = () -> run();
		long delayTicks = (long) (delaySeconds * 20);
		int bukkitTaskID = scheduler.scheduleSyncDelayedTask(plugin, runnable, delayTicks);
		setBukkitTaskID(bukkitTaskID);
	}

}
