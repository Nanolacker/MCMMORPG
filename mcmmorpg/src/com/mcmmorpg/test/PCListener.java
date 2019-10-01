package com.mcmmorpg.test;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.JsonUtils;

public class PCListener implements Listener {

	private static final String SAVE_FOLDER_NAME = "C:/Users/conno/Desktop/PlayerSaves";

	private static final Gson gson = new Gson();

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = new File(SAVE_FOLDER_NAME + "/" + player.getName());
		if (file.exists()) {
			PlayerCharacterSaveData saveData = JsonUtils.readFromFile(file, PlayerCharacterSaveData.class);
			PlayerCharacter.registerPlayerCharacter(player, saveData);
			QuestBookItem item = new QuestBookItem();
			player.getInventory().addItem(item.getItemStack());
		} else {
			createNewCharacter(player);
		}

	}

	private void createNewCharacter(Player player) {
		PlayerClass playerClass = PlayerClass.forName("Fighter");
		World world = Bukkit.getWorld("world");
		Location startingLocation = new Location(world, 141, 70, 66);
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createFreshSaveData(player, playerClass,
				startingLocation);
		PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
		pc.grantXP(90);
		new DelayedTask(4) {
			public void run() {
				pc.grantXP(50);
			}
		}.schedule();
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		File file = new File(SAVE_FOLDER_NAME + "/" + player.getName());
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createSaveData(pc);
		JsonUtils.writeToFile(file, saveData);
		pc.deactivate();
	}

}
