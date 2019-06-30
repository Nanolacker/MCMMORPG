package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;

public abstract class RepeatingTask extends CommonTask {

	private double periodSeconds;

	public RepeatingTask(double periodSeconds) {
		this.periodSeconds = periodSeconds;
	}

	@Override
	protected void scheduleBukkitTask() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		MMORPGPlugin plugin = MMORPGPlugin.getPlugin(MMORPGPlugin.class);
		Runnable runnable = () -> run();
		long periodTicks = (long) (periodSeconds * 20);
		int bukkitTaskID = scheduler.scheduleSyncRepeatingTask(plugin, runnable, 0L, periodTicks);
		setBukkitTaskID(bukkitTaskID);
	}

}
