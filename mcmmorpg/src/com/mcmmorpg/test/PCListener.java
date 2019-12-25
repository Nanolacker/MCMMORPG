package com.mcmmorpg.test;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.ItemListener;
import com.mcmmorpg.common.item.ItemManager;
import com.mcmmorpg.common.item.ItemStackFactory;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;

public class PCListener implements Listener {

	private final File saveDataDirectory;
	private final Location startingLocation;
	private final ItemStack menuItem;

	public PCListener() {
		File dataFolder = IOUtils.getDataFolder();
		saveDataDirectory = new File(dataFolder, "PlayerData");
		if (!saveDataDirectory.exists()) {
			saveDataDirectory.mkdir();
		}
		World world = Bukkit.getWorld("world");
		startingLocation = new Location(world, 141, 70, 66);

		menuItem = ItemStackFactory.create("Menu", null, Material.EMERALD);
		ItemListener menuItemListener = new ItemListener() {
			@Override
			public void onInventoryClick(InventoryClickEvent event) {
			}

			@Override
			public void onInventoryDrag(InventoryDragEvent event) {
			}

			@Override
			public void onInteract(PlayerInteractEvent event) {
				Debug.log("open menu");
			}

			@Override
			public void onPickup(InventoryPickupItemEvent event) {
			}

			@Override
			public void onDrop(PlayerDropItemEvent event) {
			}

		};
		ItemManager.registerItemListener(menuItem, menuItemListener);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = getSaveFile(player.getName());
		if (file.exists()) {
			PlayerCharacterSaveData saveData = IOUtils.jsonFromFile(file, PlayerCharacterSaveData.class);
			PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
			player.getInventory().addItem(menuItem);
			pc.setTargetQuest(Quest.forName("Saving the Farm"));
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
