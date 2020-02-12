package com.mcmmorpg.common;

import org.bukkit.plugin.java.JavaPlugin;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.time.GameClock;
import com.mcmmorpg.common.ui.TextPanel;

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
		NonPlayerCharacter.despawnAll();
		TextPanel.removeAllEntities();
		LootChest.removeAll();
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
