package com.mcmmorpg.common;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.time.GameClock;
import com.mcmmorpg.common.ui.EssentialCommands;

public abstract class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public final void onEnable() {
		isInitialized = false;
		EssentialCommands.registerEssentialCommands();
		GameClock.start();
		NonPlayerCharacter.startNPCSpawner();
		LootChest.init();
		onMMORPGStart();
		isInitialized = true;
	}

	@Override
	public final void onDisable() {
		onMMORPGStop();
		//removeAllEntities();
		kickAllPlayers();
		NonPlayerCharacter.despawnAll();
		LootChest.removeAll();
	}

	public static void removeAllEntities() {
		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			List<Entity> entities = world.getEntities();
			for (Entity entity : entities) {
				entity.remove();
			}
		}
	}

	private void kickAllPlayers() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			player.kickPlayer("Reloading Server");
		}
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

	public static MMORPGPlugin getInstance() {
		return getPlugin(MMORPGPlugin.class);
	}

	protected abstract void onMMORPGStart();

	protected abstract void onMMORPGStop();

}
