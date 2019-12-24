package com.mcmmorpg.test;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.IOUtils;

public class PCListener implements Listener {

	private final File saveDataDirectory;
	private final Location startingLocation;

	public PCListener() {
		File dataFolder = IOUtils.getDataFolder();
		saveDataDirectory = new File(dataFolder, "PlayerData");
		if (!saveDataDirectory.exists()) {
			saveDataDirectory.mkdir();
		}
		World world = Bukkit.getWorld("world");
		startingLocation = new Location(world, 141, 70, 66);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = getSaveFile(player.getName());
		if (file.exists()) {
			PlayerCharacterSaveData saveData = IOUtils.jsonFromFile(file, PlayerCharacterSaveData.class);
			PlayerCharacter.registerPlayerCharacter(player, saveData);
		} else {
			createNewCharacter(player);
		}

	}

	private void createNewCharacter(Player player) {
		PlayerClass playerClass = PlayerClass.forName("Fighter");
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createFreshSaveData(player, playerClass,
				startingLocation);
		PlayerCharacter.registerPlayerCharacter(player, saveData);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		File file = getSaveFile(player.getName());
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createSaveData(pc);
		IOUtils.jsonToFile(file, saveData);
		pc.deactivate();
	}

	private File getSaveFile(String playerName) {
		File saveFile = new File(saveDataDirectory, playerName + ".json");
		return saveFile;
	}

}
