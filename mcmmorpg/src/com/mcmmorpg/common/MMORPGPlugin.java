package com.mcmmorpg.common;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.time.Clock;
import com.mcmmorpg.common.ui.EssentialCommands;
import com.mcmmorpg.common.ui.PlayerInteractionListener;

/**
 * The center of an MCMMORPG implementation that will handle the essential start
 * up and shut down procedures.
 */
public abstract class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public final void onEnable() {
		isInitialized = false;
		removeAllEntities();
		EssentialCommands.registerEssentialCommands();
		Clock.start();
		NonPlayerCharacter.startSpawner();
		LootChest.startSpawner();
		EventManager.registerEvents(new PlayerInteractionListener());
		onMMORPGStart();
		isInitialized = true;
	}

	@Override
	public final void onDisable() {
		onMMORPGStop();
		kickAllPlayers();
		NonPlayerCharacter.despawnAll();
		LootChest.removeAll();
	}

	/**
	 * Remove all spawned entities for cleanup.
	 */
	private static void removeAllEntities() {
		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			List<Entity> entities = world.getEntities();
			for (Entity entity : entities) {
				entity.remove();
			}
		}
	}

	/**
	 * Removes all players from the server so that it can properly shut down. This
	 * is mostly only important for development.
	 */
	private void kickAllPlayers() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			player.kickPlayer("Reloading Server");
		}
	}

	/**
	 * Returns whether or not the plugin has fully started up.
	 */
	public static boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Returns the MCMMORPG plugin in use.
	 */
	public static MMORPGPlugin getInstance() {
		return getPlugin(MMORPGPlugin.class);
	}

	/**
	 * Invoked after the plugin starts up.
	 */
	protected abstract void onMMORPGStart();

	/**
	 * Invoked before the plugin shuts down.
	 */
	protected abstract void onMMORPGStop();

}
