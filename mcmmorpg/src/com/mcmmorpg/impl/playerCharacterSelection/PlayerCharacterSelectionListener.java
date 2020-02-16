package com.mcmmorpg.impl.playerCharacterSelection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.playerCharacterSelection.PlayerCharacterSelectionProfile.Menu;

public class PlayerCharacterSelectionListener implements Listener {

	private static final File PLAYER_CHARACTER_DATA_DIRECTORY;
	private static final Location CHARACTER_SELECTION_LOCATION;
	private static final ItemStack DELETE_CHARACTER_ITEM_STACK;
	private static final ItemStack GO_BACK_ITEM_STACK;
	private static final Inventory PLAYER_CLASS_SELECT_INVENTORY;
	private static final Inventory DELETE_CONFIRM_INVENTORY;

	private static final String STARTING_ZONE;
	private static final Location STARTING_LOCATION;
	private static final PotionEffect INVISIBILITY;

	static {
		PLAYER_CHARACTER_DATA_DIRECTORY = new File(IOUtils.getDataFolder(), "playerSaveData");
		if (!PLAYER_CHARACTER_DATA_DIRECTORY.exists()) {
			PLAYER_CHARACTER_DATA_DIRECTORY.mkdir();
		}
		CHARACTER_SELECTION_LOCATION = new Location(Worlds.CHARACTER_SELECTION, 0, 64, 0);
		DELETE_CHARACTER_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Delete Character",
				ChatColor.GRAY + "Open the character deletion menu", Material.BARRIER);
		GO_BACK_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Go back", null, Material.BARRIER);

		PLAYER_CLASS_SELECT_INVENTORY = Bukkit.createInventory(null, 9, "Select a Class");
		ItemStack selectFighterItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Fighter",
				ChatColor.GRAY + "Excels at martial combat", Material.IRON_SWORD);
		ItemStack selectMageItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Mage",
				ChatColor.GRAY + "Uses magic and excels at ranged combat", Material.STICK);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(2, selectFighterItemStack);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(6, selectMageItemStack);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(8, GO_BACK_ITEM_STACK);

		DELETE_CONFIRM_INVENTORY = Bukkit.createInventory(null, 9, "Are you sure?");
		ItemStack confirmItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Confirm", null, Material.EMERALD);
		ItemStack cancelItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Cancel", null, Material.BARRIER);
		DELETE_CONFIRM_INVENTORY.setItem(2, confirmItemStack);
		DELETE_CONFIRM_INVENTORY.setItem(6, cancelItemStack);
		STARTING_ZONE = ChatColor.GREEN + "Flinton";
		STARTING_LOCATION = new Location(Worlds.ELADRADOR, 0, 64, 0);
		INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
	}

	private Map<Player, PlayerCharacterSelectionProfile> profileMap = new HashMap<>();

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.addPotionEffect(INVISIBILITY);
		player.teleport(CHARACTER_SELECTION_LOCATION);
		File playerFolder = getPlayerFolder(player);
		if (!playerFolder.exists()) {
			playerFolder.mkdir();
		}
		PlayerCharacterSelectionProfile profile = new PlayerCharacterSelectionProfile(player);
		profileMap.put(player, profile);
		openCharacterSelectInventory(player);
	}

	private File getPlayerFolder(Player player) {
		return new File(PLAYER_CHARACTER_DATA_DIRECTORY, player.getName());
	}

	private Inventory getCharacterSelectInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(player, 9, "Select Character");
		inventory.setItem(0, getCharacterSelectItemStack(player, 1));
		inventory.setItem(2, getCharacterSelectItemStack(player, 2));
		inventory.setItem(4, getCharacterSelectItemStack(player, 3));
		inventory.setItem(6, getCharacterSelectItemStack(player, 4));
		inventory.setItem(8, DELETE_CHARACTER_ITEM_STACK);
		return inventory;
	}

	private ItemStack getCharacterSelectItemStack(Player player, int characterSlot) {
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		PersistentPlayerCharacterDataContainer data = profile.getCharacterData(characterSlot);
		String title = ChatColor.GOLD + "Character " + characterSlot;
		if (data == null) {
			return ItemFactory.createItemStack(title, ChatColor.GRAY + "Create new character", Material.GLASS);
		} else {
			PlayerClass playerClass = data.getPlayerClass();
			Material material = materialForPlayerClass(playerClass);
			int level = PlayerCharacter.xpToLevel(data.getXP());
			String lore = ChatColor.GREEN + "Class: " + playerClass.getName() + ChatColor.GOLD + "\nLevel " + level
					+ ChatColor.GREEN + "\nZone: " + data.getZone();
			return ItemFactory.createItemStack(title, lore, material);
		}
	}

	private Material materialForPlayerClass(PlayerClass playerClass) {
		String playerClassName = playerClass.getName();
		if (playerClassName.equals("Fighter")) {
			return Material.IRON_SWORD;
		} else if (playerClass.getName().equals("Mage")) {
			return Material.STICK;
		}
		return null;
	}

	private Inventory getCharacterDeleteInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(player, 9, "Delete Which Character?");
		inventory.setItem(0, getCharacterSelectItemStack(player, 1));
		inventory.setItem(2, getCharacterSelectItemStack(player, 2));
		inventory.setItem(4, getCharacterSelectItemStack(player, 3));
		inventory.setItem(6, getCharacterSelectItemStack(player, 4));
		inventory.setItem(8, DELETE_CHARACTER_ITEM_STACK);
		return inventory;
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.getWorld() != CHARACTER_SELECTION_LOCATION.getWorld()) {
			return;
		}
		event.setCancelled(true);
		int clickedSlot = event.getSlot();
		if (event.getClickedInventory().getItem(clickedSlot) == null) {
			return;
		}
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		Menu openMenu = profile.getOpenMenu();
		if (openMenu == Menu.CONFIRM_DELETION) {
			if (clickedSlot == 2) {
				player.sendMessage(ChatColor.YELLOW + "Deleting...");
				int characterSlot = profile.getCurrentCharacterSlot();
				deleteCharacter(player, characterSlot);
				player.sendMessage(ChatColor.GREEN + "Done!");
				openCharacterSelectInventory(player);
			} else if (clickedSlot == 6) {
				player.sendMessage(ChatColor.RED + "Cancelled");
				openCharacterSelectInventory(player);
			}
		} else if (openMenu == Menu.DELETING_CHARACTER) {
			if (clickedSlot % 2 == 0 && clickedSlot != 8) {
				int characterSlot = clickedSlot / 2 + 1;
				profile.setOpenMenu(Menu.CONFIRM_DELETION);
				profile.setCurrentCharacterSlot(characterSlot);
				player.openInventory(DELETE_CONFIRM_INVENTORY);
			}
		} else if (openMenu == Menu.SELECT_CHARACTER) {
			if (clickedSlot % 2 == 0 && clickedSlot < 7) {
				int characterSlot = clickedSlot / 2 + 1;
				profile.setCurrentCharacterSlot(characterSlot);
				PersistentPlayerCharacterDataContainer data = profile.getCharacterData(characterSlot);
				if (data == null) {
					profile.setOpenMenu(Menu.SELECT_PLAYER_CLASS);
					player.openInventory(PLAYER_CLASS_SELECT_INVENTORY);
				} else {
					selectCharacter(player, data);
				}
			}
		} else if (openMenu == Menu.SELECT_PLAYER_CLASS) {
			PlayerClass playerClass;
			if (clickedSlot == 2) {
				playerClass = PlayerClass.forName("Fighter");
			} else if (clickedSlot == 6) {
				playerClass = PlayerClass.forName("Mage");
			} else {
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Creating new character...");
			int characterSlot = profile.getCurrentCharacterSlot();
			createNewCharacter(player, playerClass, characterSlot);
			profile.updateCharacterData(characterSlot);
			player.sendMessage(ChatColor.GREEN + "Done!");
			openCharacterSelectInventory(player);
		}
	}

	private void createNewCharacter(Player player, PlayerClass playerClass, int characterSlot) {
		File characterSaveFile = getCharacterSaveFile(player, characterSlot);
		IOUtils.createFile(characterSaveFile);
		PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createFreshSaveData(player,
				playerClass, STARTING_ZONE, STARTING_LOCATION);
		IOUtils.writeJson(characterSaveFile, data);
	}

	static File getCharacterSaveFile(Player player, int characterSlot) {
		File playerFolder = new File(PLAYER_CHARACTER_DATA_DIRECTORY, player.getName());
		File characterSaveFile = new File(playerFolder, "characterSlot" + characterSlot + ".json");
		return characterSaveFile;
	}

	private void deleteCharacter(Player player, int characterSlot) {

	}

	private void openCharacterSelectInventory(Player player) {
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		profile.setOpenMenu(Menu.SELECT_CHARACTER);
		Inventory inventory = getCharacterSelectInventory(player);
		player.openInventory(inventory);
	}

	private void selectCharacter(Player player, PersistentPlayerCharacterDataContainer data) {
		profileMap.remove(player);
		PlayerCharacter.registerPlayerCharacter(player, data);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		Inventory inventory = player.getInventory();
		// add menu
	}

	@EventHandler
	private void onCloseInventory(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getWorld() == CHARACTER_SELECTION_LOCATION.getWorld()) {
			if (player.getOpenInventory() instanceof PlayerInventory) {
				openCharacterSelectInventory(player);
			}
		}
	}

	/*
	 * - select character menu - slot 1 - slot 2 - slot 3 - slot 4 - delete
	 * character - select player class menu - fighter - mage - back - delete
	 * character menu - slot 1 - slot 2 - slot 3 - slot 4 - back - delete confirm
	 * menu - confirm - cancel
	 * 
	 */

}
