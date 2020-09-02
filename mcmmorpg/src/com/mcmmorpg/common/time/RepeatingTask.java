package com.mcmmorpg.common.time;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.util.MathUtility;

/**
 * A task that will repeat with a specified period.
 */
public abstract class RepeatingTask extends Task {

	private double period;

	/**
	 * A task that will repeat with the specified period.
	 */
	public RepeatingTask(double period) {
		this.period = period;
	}

	@Override
	protected final void scheduleBukkitTask() {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		MMORPGPlugin plugin = MMORPGPlugin.getInstance();
		Runnable runnable = () -> run();
		long periodTicks = MathUtility.secondsToTicks(period);
		int bukkitTaskID = scheduler.scheduleSyncRepeatingTask(plugin, runnable, 0L, periodTicks);
		setBukkitTaskID(bukkitTaskID);
	}

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
		if (isScheduled()) {
			cancel();
			schedule();
		}
	}

}
