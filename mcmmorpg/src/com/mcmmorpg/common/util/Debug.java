package com.mcmmorpg.common.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.time.DelayedTask;

public class Debug {

	private static final double DRAWING_PARTICLE_SPACE_DISTANCE = 0.25;

	private Debug() {
		// no instances
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
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		String message = String.format(format, args);
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
		Server server = Bukkit.getServer();
		String formattedMessage = ChatColor.AQUA + "[DEBUG]: " + ChatColor.RESET + message + "\n" + ChatColor.AQUA
				+ caller.toString();
		if (initialized) {
			server.broadcastMessage(formattedMessage);
		} else {
			new DelayedTask() {
				@Override
				protected void run() {
					server.broadcastMessage(formattedMessage);
				}
			}.schedule();
		}
	}

	/**
	 * Returns the first online player this method finds. Provides a convenient way
	 * to find a player for testing purposes.
	 */
	public static Player getAPlayer() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		if (players.isEmpty()) {
			return null;
		}
		return (Player) players.toArray()[0];
	}

	public static void drawRay(Ray ray, Particle particle) {
		Location start = ray.getStart();
		Location end = ray.getEnd();
		World world = start.getWorld();
		Vector v = end.clone().subtract(start).toVector();
		double length = v.length();
		Vector direction = v.normalize();
		int particleCount = (int) (length / DRAWING_PARTICLE_SPACE_DISTANCE);
		Location particleLocation = start.clone();
		Vector increment = direction.multiply(DRAWING_PARTICLE_SPACE_DISTANCE);
		for (int i = 0; i < particleCount; i++) {
			world.spawnParticle(particle, particleLocation, 0);
			particleLocation.add(increment);
		}
	}

	public static void drawCollider(Collider collider, Particle particle) {
		World world = collider.getWorld();
		// whether xCount has reached reached its maximum
		boolean xFinished = false;
		for (double xCount = collider.getXMin(); xCount <= collider.getXMax()
				&& !xFinished; xCount += DRAWING_PARTICLE_SPACE_DISTANCE) {
			// whether yCount has reached reached its maximum
			boolean yFinished = false;
			for (double yCount = collider.getYMin(); yCount <= collider.getYMax()
					&& !yFinished; yCount += DRAWING_PARTICLE_SPACE_DISTANCE) {
				// whether zCount has reached reached its maximum
				boolean zFinished = false;
				for (double zCount = collider.getZMin(); zCount <= collider.getZMax()
						&& !zFinished; zCount += DRAWING_PARTICLE_SPACE_DISTANCE) {
					int validCount = 0;
					if (xCount == collider.getXMin()) {
						validCount++;
					}
					if (xCount > collider.getXMax() - DRAWING_PARTICLE_SPACE_DISTANCE) {
						validCount++;
						xFinished = true;
					}
					if (yCount == collider.getYMin()) {
						validCount++;
					}
					if (yCount > collider.getYMax() - DRAWING_PARTICLE_SPACE_DISTANCE) {
						validCount++;
						yFinished = true;
					}
					if (zCount == collider.getZMin()) {
						validCount++;
					}
					if (zCount > collider.getZMax() - DRAWING_PARTICLE_SPACE_DISTANCE) {
						validCount++;
						zFinished = true;
					}
					boolean validPoint = validCount >= 2;
					if (validPoint) {
						Location point = new Location(world, xCount, yCount, zCount);
						world.spawnParticle(particle, point, 0);
					}
				}
			}
		}
	}

}
