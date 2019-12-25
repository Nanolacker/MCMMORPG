package com.mcmmorpg.common;

import org.bukkit.plugin.java.JavaPlugin;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.time.GameClock;
import com.mcmmorpg.common.ui.TextArea;

public abstract class MMORPGPlugin extends JavaPlugin {

	private static boolean isInitialized;

	@Override
	public void onEnable() {
		isInitialized = false;
		GameClock.start();
		NonPlayerCharacter.startSpawnTask();
		try {
			onMMORPGStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isInitialized = true;
	}

	@Override
	public void onDisable() {
		try {
			onMMORPGStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		NonPlayerCharacter.despawnAll();
		TextArea.removeAllEntities();
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
