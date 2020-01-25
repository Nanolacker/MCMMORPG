package com.mcmmorpg.impl.listeners;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.Worlds;

public class PlayerCharacterSelectionListener implements Listener {

	private static final File PLAYER_CHARACTER_DATA_DIRECTORY;
	private static final Location LOBBY_SPAWN;
	private static final ItemStack CREATE_NEW_CHARACTER;
	private static final ItemStack SELECT_EXISTING_CHARACTER;

	private static final PlayerClass DEFAULT_PLAYER_CLASS;
	private static final Location START_LOCATION;
	private static final ItemStack OPEN_QUEST_LOG;
	private static final ItemStack MENU;

	static {
		PLAYER_CHARACTER_DATA_DIRECTORY = new File(IOUtils.getDataFolder(), "player_save_data");
		if (!PLAYER_CHARACTER_DATA_DIRECTORY.exists()) {
			PLAYER_CHARACTER_DATA_DIRECTORY.mkdir();
		}
		LOBBY_SPAWN = new Location(Worlds.LOBBY, 0, 0, 0);
		CREATE_NEW_CHARACTER = ItemFactory.createItemStack(ChatColor.GREEN + "Create new character", null,
				Material.DIAMOND);
		ItemFactory.registerStaticInteractable(CREATE_NEW_CHARACTER);
		SELECT_EXISTING_CHARACTER = ItemFactory.createItemStack(ChatColor.GREEN + "Login", null, Material.EMERALD);
		ItemFactory.registerStaticInteractable(SELECT_EXISTING_CHARACTER);

		DEFAULT_PLAYER_CLASS = PlayerClass.forName("Fighter");
		START_LOCATION = new Location(Worlds.ELADRADOR, 0, 70, 0);
		MENU = ItemFactory.createItemStack(ChatColor.GREEN + "Menu", null, Material.EMERALD);
		ItemFactory.registerStaticInteractable(MENU);
		OPEN_QUEST_LOG = ItemFactory.createItemStack(ChatColor.GREEN + "Quest Log", null, Material.COMPASS);
		ItemFactory.registerStaticInteractable(OPEN_QUEST_LOG);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage("Test join message");
		Player player = event.getPlayer();
		player.teleport(LOBBY_SPAWN);
		Inventory inventory = player.getInventory();
		if (playerCharacterIsCreatedBy(player)) {
			inventory.setItem(0, SELECT_EXISTING_CHARACTER);
		} else {
			inventory.setItem(0, CREATE_NEW_CHARACTER);
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage("Test quit message");
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc != null) {
			savePlayerCharacterData(pc);
		}
	}

	private File getDataFile(String playerName) {
		File file = new File(PLAYER_CHARACTER_DATA_DIRECTORY, playerName + ".json");
		return file;
	}

	private boolean playerCharacterIsCreatedBy(Player player) {
		return getDataFile(player.getName()).exists();
	}

	@EventHandler
	private void onCreateNewPlayerCharacter(StaticInteractableEvent event) {
		ItemStack interactable = event.getInteractable();
		if (interactable.equals(CREATE_NEW_CHARACTER)) {
			Player player = event.getPlayer();
			player.sendMessage(ChatColor.GREEN + "Creating new character...");
			PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer
					.createFreshSaveData(player, DEFAULT_PLAYER_CLASS, START_LOCATION);
			loadPlayerCharacter(player, data);
		}
	}

	@EventHandler
	private void onSelectExistingCharacter(StaticInteractableEvent event) {
		ItemStack interactable = event.getInteractable();
		if (interactable.equals(SELECT_EXISTING_CHARACTER)) {
			Player player = event.getPlayer();
			player.sendMessage(ChatColor.GREEN + "Logging in...");
			File dataFile = getDataFile(player.getName());
			PersistentPlayerCharacterDataContainer data = IOUtils.jsonFromFile(dataFile,
					PersistentPlayerCharacterDataContainer.class);
			loadPlayerCharacter(player, data);
		}
	}

	private void loadPlayerCharacter(Player player, PersistentPlayerCharacterDataContainer data) {
		Inventory inventory = player.getInventory();
		inventory.clear();
		PlayerCharacter.registerPlayerCharacter(player, data);
		inventory.setItem(7, OPEN_QUEST_LOG);
		inventory.setItem(8, MENU);
	}

	private void savePlayerCharacterData(PlayerCharacter pc) {
		File dataFile = getDataFile(pc.getName());
		PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createSaveData(pc);
		IOUtils.jsonToFile(dataFile, data);
	}

	@EventHandler
	private void onOpenQuestLog(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(OPEN_QUEST_LOG)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			pc.openQuestLog();
		}
	}

	@EventHandler
	private void onOpenMenu(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(MENU)) {
			Debug.log("Open menu");
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		}
	}

}
