package com.mcmmorpg.common.time;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Superclass for delayed tasks and repeating tasks.
 */
public abstract class Task {
    private boolean scheduled;
    BukkitRunnable runnable;

    Task() {
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
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Task.this.run();
            }
        };
    }

    /**
     * Returns whether this task is scheduled to run.
     */
    public final boolean isScheduled() {
        return scheduled;
    }

    /**
     * Stops this task from running, rendering it not scheduled. Throws an
     * IllegalStateException if this task is not currently scheduled. This task can
     * be rescheduled after being cancelled.
     */
    public final void cancel() {
        if (!scheduled) {
            throw new IllegalStateException("Cannot cancel a task that isn't scheduled");
        }
        runnable.cancel();
        scheduled = false;
    }

    /**
     * What will be run when this task executes.
     */
    protected abstract void run();
}
