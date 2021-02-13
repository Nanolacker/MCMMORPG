package com.mcmmorpg.common.time;

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
    public final void schedule() {
        super.schedule();
        runnable.runTaskTimer(MMORPGPlugin.getInstance(), 0L, MathUtility.secondsToTicks(period));
    }

    public final double getPeriod() {
        return period;
    }

    public final void setPeriod(double period) {
        this.period = period;
        if (isScheduled()) {
            cancel();
            schedule();
        }
    }
}
