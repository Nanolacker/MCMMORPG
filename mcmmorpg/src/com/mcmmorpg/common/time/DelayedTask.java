package com.mcmmorpg.common.time;

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
	public final void schedule() {
		super.schedule();
		runnable.runTaskLater(MMORPGPlugin.getInstance(), MathUtility.secondsToTicks(delay));
	}

}
