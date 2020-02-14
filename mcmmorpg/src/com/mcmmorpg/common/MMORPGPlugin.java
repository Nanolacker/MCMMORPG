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
import com.mcmmorpg.common.ui.TextPanel;

import net.md_5.bungee.api.ChatColor;

public abstract class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public final void onEnable() {
		isInitialized = false;
		GameClock.start();
		NonPlayerCharacter.startNPCSpawner();
		LootChest.init();
		try {
			onMMORPGStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isInitialized = true;
	}

	@Override
	public final void onDisable() {
		try {
			onMMORPGStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		kickAllPlayers();
		NonPlayerCharacter.despawnAll();
		LootChest.removeAll();
		TextPanel.removeAllEntities();
		removeAllEntities();
	}

	private void kickAllPlayers() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			player.kickPlayer("Reloading Server");
		}
	}

	private static void removeAllEntities() {
		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			List<Entity> entities = world.getEntities();
			for (Entity entity : entities) {
				entity.remove();
			}
		}
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

	public static MMORPGPlugin getPlugin() {
		return getPlugin(MMORPGPlugin.class);
	}

	protected abstract void onMMORPGStart();

	protected abstract void onMMORPGStop();

}
