package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;

public abstract class DelayedTask extends CommonTask {

	private double delaySeconds;

	public DelayedTask(double delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public DelayedTask() {
		this(0.0);
	}

	@Override
	protected void scheduleBukkitTask() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		Plugin plugin = MMORPGPlugin.getPlugin(MMORPGPlugin.class);
		Runnable runnable = () -> run();
		long delayTicks = (long) (delaySeconds * 20);
		int bukkitTaskID = scheduler.scheduleSyncDelayedTask(plugin, runnable, delayTicks);
		setBukkitTaskID(bukkitTaskID);
	}

}
