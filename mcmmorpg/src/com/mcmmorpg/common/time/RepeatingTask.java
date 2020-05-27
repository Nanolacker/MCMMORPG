package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.utils.MathUtils;

/**
 * A task that will repeat with a specified period.
 */
public abstract class RepeatingTask extends CommonTask {

	private double periodSeconds;

	/**
	 * A task that will repeat with the specified period.
	 */
	public RepeatingTask(double periodSeconds) {
		this.periodSeconds = periodSeconds;
	}

	@Override
	protected final void scheduleBukkitTask() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		MMORPGPlugin plugin = MMORPGPlugin.getPlugin(MMORPGPlugin.class);
		Runnable runnable = () -> run();
		long periodTicks = MathUtils.secondsToTicks(periodSeconds);
		int bukkitTaskID = scheduler.scheduleSyncRepeatingTask(plugin, runnable, 0L, periodTicks);
		setBukkitTaskID(bukkitTaskID);
	}

}
