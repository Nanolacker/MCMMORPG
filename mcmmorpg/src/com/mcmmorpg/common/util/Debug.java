package com.mcmmorpg.common.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.ai.Path;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;

/**
 * Provides static methods that ease the development and debugging process.
 */
public class Debug {
    private Debug() {
    }

    /**
     * Prints the message to the console and players, including the stack trace
     * element from which this method was called for ease of debugging.
     */
    public static void log(Object message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        log0(message, caller);
    }

    /**
     * Prints the formatted message to the console and players, including the stack
     * trace element from which this method was called for ease of debugging.
     */
    public static void logf(String format, Object... args) {
        String message = String.format(format, args);
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        log0(message, caller);
    }

    /**
     * Logs how much memory has been used of the available memory.
     */
    public static void logMemoryUsage() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long usedMemory = maxMemory - freeMemory;
        double usedMemoryGigabytes = usedMemory * 0.000000001;
        double maxMemoryGigabytes = maxMemory * 0.000000001;
        String message = usedMemoryGigabytes + " / " + maxMemoryGigabytes + " GB used";
        log0(message, caller);
    }

    private static void log0(Object message, StackTraceElement caller) {
        boolean initialized = MMORPGPlugin.isInitialized();
        String formattedMessage = ChatColor.AQUA + "[DEBUG]: " + ChatColor.RESET + message + "\n" + ChatColor.AQUA
                + caller.toString();
        if (initialized) {
            Bukkit.broadcastMessage(formattedMessage);
        } else {
            new DelayedTask() {
                @Override
                protected void run() {
                    Bukkit.broadcastMessage(formattedMessage);
                }
            }.schedule();
        }
    }

    /**
     * Returns the first online player found. Provides a convenient way to find a
     * player for testing purposes.
     */
    public static Player getAPlayer() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return null;
        }
        return (Player) players.toArray()[0];
    }

    public static void drawPath(Path path, Particle particle, double duration) {
        RepeatingTask drawTask = new RepeatingTask(0.1) {
            @Override
            protected void run() {
                Location[] waypoints = path.getWaypoints();
                if (waypoints.length < 2) {
                    return;
                }
                Location lineStart = waypoints[0].clone().add(0.0, 0.1, 0.0);
                for (int i = 1; i < waypoints.length; i++) {
                    Location lineEnd = waypoints[i].clone().add(0.0, 0.1, 0.0);
                    ParticleEffects.line(particle, 4.0, lineStart, lineEnd);
                    lineStart = lineEnd;
                }
            }
        };
        drawTask.schedule();
        DelayedTask cancelTask = new DelayedTask(duration) {
            @Override
            protected void run() {
                drawTask.cancel();
            }
        };
        cancelTask.schedule();
    }
}
