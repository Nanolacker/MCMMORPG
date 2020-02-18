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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.ui.ActionBarText;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.playerCharacterSelection.PlayerCharacterSelectionProfile.Menu;

public class PlayerCharacterSelectionListener implements Listener {

	private static final File PLAYER_CHARACTER_DATA_DIRECTORY;
	private static final Location CHARACTER_SELECTION_LOCATION;

	private static final ItemStack OPEN_CHARACTER_SELECT_ITEM_STACK;
	private static final ItemStack DELETE_CHARACTER_ITEM_STACK;
	private static final ItemStack GO_BACK_ITEM_STACK;

	private static final ItemStack OPEN_MENU_ITEM_STACK;
	private static final ItemStack OPEN_QUEST_LOG_ITEM_STACK;
	private static final ItemStack OPEN_SKILL_TREE_ITEM_STACK;
	private static final ItemStack CHANGE_CHARACTER_SELECTION_ITEM_STACK;
	private static final Inventory MENU_INVENTORY;

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
		OPEN_CHARACTER_SELECT_ITEM_STACK = ItemFactory.createItemStack(ChatColor.GREEN + "Select Character", null,
				Material.EMERALD);
		ItemFactory.registerStaticInteractable(OPEN_CHARACTER_SELECT_ITEM_STACK);
		DELETE_CHARACTER_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Delete Character",
				ChatColor.GRAY + "Open the character deletion menu", Material.BARRIER);
		GO_BACK_ITEM_STACK = ItemFactory.createItemStack(ChatColor.RED + "Go back", null, Material.BARRIER);

		OPEN_MENU_ITEM_STACK = ItemFactory.createItemStack(ChatColor.YELLOW + "Menu", null, Material.COMPASS);
		ItemFactory.registerStaticInteractable(OPEN_MENU_ITEM_STACK);
		OPEN_QUEST_LOG_ITEM_STACK = ItemFactory.createItemStack(ChatColor.YELLOW + "Quest Log",
				ChatColor.GRAY + "View and track your status on quests", Material.BOOK);
		ItemFactory.registerStaticInteractable(OPEN_QUEST_LOG_ITEM_STACK);
		OPEN_SKILL_TREE_ITEM_STACK = ItemFactory.createItemStack(ChatColor.YELLOW + "Skill Tree",
				ChatColor.GRAY + "Unlock and upgrade powerful skills", Material.OAK_SAPLING);
		ItemFactory.registerStaticInteractable(OPEN_SKILL_TREE_ITEM_STACK);
		CHANGE_CHARACTER_SELECTION_ITEM_STACK = ItemFactory.createItemStack(ChatColor.YELLOW + "Change Character",
				ChatColor.GRAY + "Save and return to character selection", Material.PLAYER_HEAD);
		ItemFactory.registerStaticInteractable(CHANGE_CHARACTER_SELECTION_ITEM_STACK);
		MENU_INVENTORY = Bukkit.createInventory(null, 9, "Menu");
		MENU_INVENTORY.setItem(1, OPEN_QUEST_LOG_ITEM_STACK);
		MENU_INVENTORY.setItem(4, OPEN_SKILL_TREE_ITEM_STACK);
		MENU_INVENTORY.setItem(7, CHANGE_CHARACTER_SELECTION_ITEM_STACK);

		PLAYER_CLASS_SELECT_INVENTORY = Bukkit.createInventory(null, 9, "Select a Class");
		ItemStack selectFighterItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Fighter",
				ChatColor.GRAY + "Excels at martial combat", Material.IRON_SWORD);
		ItemStack selectMageItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Mage",
				ChatColor.GRAY + "Uses magic and excels at ranged combat", Material.STICK);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(2, selectFighterItemStack);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(6, selectMageItemStack);
		PLAYER_CLASS_SELECT_INVENTORY.setItem(8, GO_BACK_ITEM_STACK);

		DELETE_CONFIRM_INVENTORY = Bukkit.createInventory(null, 9, "Are you sure?");
		ItemStack confirmItemStack = ItemFactory.createItemStack(ChatColor.GREEN + "Confirm",
				ChatColor.GRAY + "Yes, delete this character", Material.EMERALD);
		ItemStack cancelItemStack = ItemFactory.createItemStack(ChatColor.RED + "Cancel",
				ChatColor.GRAY + "No, do not delete this character", Material.BARRIER);
		DELETE_CONFIRM_INVENTORY.setItem(2, confirmItemStack);
		DELETE_CONFIRM_INVENTORY.setItem(6, cancelItemStack);
		STARTING_ZONE = ChatColor.GREEN + "Flinton";
		STARTING_LOCATION = new Location(Worlds.ELADRADOR, 0, 64, 0);
		INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
	}

	private Map<Player, PlayerCharacterSelectionProfile> profileMap = new HashMap<>();

	private void sendToCharacterSelection(Player player) {
		player.addPotionEffect(INVISIBILITY);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(1);
		player.setExp(0);
		ActionBarText emptyActionBarText = new ActionBarText("");
		emptyActionBarText.apply(player);
		player.teleport(CHARACTER_SELECTION_LOCATION);
		Inventory inventory = player.getInventory();
		player.getInventory().clear();
		inventory.addItem(OPEN_CHARACTER_SELECT_ITEM_STACK);
		File playerFolder = getPlayerFolder(player);
		if (!playerFolder.exists()) {
			playerFolder.mkdir();
		}
		PlayerCharacterSelectionProfile profile = new PlayerCharacterSelectionProfile(player);
		profileMap.put(player, profile);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		sendToCharacterSelection(player);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Player player = event.getPlayer();
		profileMap.remove(player);
	}

	@EventHandler
	private void onInteractWithMenu(StaticInteractableEvent event) {
		ItemStack interactable = event.getInteractable();
		if (interactable.equals(OPEN_MENU_ITEM_STACK)) {
			Player player = event.getPlayer();
			player.openInventory(MENU_INVENTORY);
		} else if (interactable.equals(OPEN_QUEST_LOG_ITEM_STACK)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			pc.openQuestLog();
		} else if (interactable.equals(OPEN_SKILL_TREE_ITEM_STACK)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			pc.getPlayerClass().getSkillTree().open(pc);
		} else if (interactable.equals(CHANGE_CHARACTER_SELECTION_ITEM_STACK)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			removePlayerCharacter(pc);
			sendToCharacterSelection(player);
		}
	}

	@EventHandler
	private void onOpenCharacterSelectInventory(StaticInteractableEvent event) {
		if (event.getInteractable().equals(OPEN_CHARACTER_SELECT_ITEM_STACK)) {
			openCharacterSelectInventory(event.getPlayer());
		}
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
					+ ChatColor.GREEN + "\nZone: " + data.getZone() + ChatColor.GRAY
					+ "\n\nClick to play this character";
			return ItemFactory.createItemStack(title, lore, material);
		}
	}

	private ItemStack getCharacterDeleteItemStack(Player player, int characterSlot) {
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		PersistentPlayerCharacterDataContainer data = profile.getCharacterData(characterSlot);
		String title = ChatColor.GOLD + "Character " + characterSlot;
		if (data == null) {
			return ItemFactory.createItemStack(title, ChatColor.GRAY + "No character created", Material.GLASS);
		} else {
			PlayerClass playerClass = data.getPlayerClass();
			Material material = Material.BARRIER;
			int level = PlayerCharacter.xpToLevel(data.getXP());
			String lore = ChatColor.GREEN + "Class: " + playerClass.getName() + ChatColor.GOLD + "\nLevel " + level
					+ ChatColor.GREEN + "\nZone: " + data.getZone() + ChatColor.GRAY
					+ "\n\nClick to delete this character";
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
		inventory.setItem(0, getCharacterDeleteItemStack(player, 1));
		inventory.setItem(2, getCharacterDeleteItemStack(player, 2));
		inventory.setItem(4, getCharacterDeleteItemStack(player, 3));
		inventory.setItem(6, getCharacterDeleteItemStack(player, 4));
		inventory.setItem(8, GO_BACK_ITEM_STACK);
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
		Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getItem(clickedSlot) == null) {
			return;
		}
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		Menu openMenu = profile.getOpenMenu();
		if (openMenu == Menu.CONFIRM_DELETION) {
			if (clickedSlot == 2) {
				int characterSlot = profile.getCurrentCharacterSlot();
				deleteCharacter(player, characterSlot);
				player.sendMessage(ChatColor.YELLOW + "Deleted " + ChatColor.GOLD + "Character " + characterSlot
						+ ChatColor.YELLOW + "!");
				openCharacterSelectInventory(player);
			} else if (clickedSlot == 6) {
				openCharacterSelectInventory(player);
			}
		} else if (openMenu == Menu.DELETING_CHARACTER) {
			if (clickedSlot == 8) {
				// go back
				openCharacterSelectInventory(player);
			} else if (clickedSlot % 2 == 0) {
				int characterSlot = clickedSlot / 2 + 1;
				if (profile.getCharacterData(characterSlot) == null) {
					player.sendMessage(ChatColor.RED + "No character created");
				} else {
					profile.setOpenMenu(Menu.CONFIRM_DELETION);
					profile.setCurrentCharacterSlot(characterSlot);
					player.openInventory(DELETE_CONFIRM_INVENTORY);
				}
			}
		} else if (openMenu == Menu.SELECT_CHARACTER) {
			if (clickedSlot == 8) {
				openCharacterDeleteInventory(player);
			} else if (clickedSlot % 2 == 0) {
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
			} else if (clickedSlot == 8) {
				// go back
				openCharacterSelectInventory(player);
				return;
			} else {
				return;
			}
			int characterSlot = profile.getCurrentCharacterSlot();
			createNewCharacter(player, playerClass, characterSlot);
			player.sendMessage(ChatColor.YELLOW + "Created " + ChatColor.GOLD + "Character " + characterSlot
					+ ChatColor.YELLOW + "!");
			profile.updateCharacterData(characterSlot);
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
		File characterSaveFile = getCharacterSaveFile(player, characterSlot);
		characterSaveFile.delete();
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		profile.updateCharacterData(characterSlot);
	}

	private void openCharacterSelectInventory(Player player) {
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		profile.setOpenMenu(Menu.SELECT_CHARACTER);
		Inventory inventory = getCharacterSelectInventory(player);
		player.openInventory(inventory);
	}

	private void openCharacterDeleteInventory(Player player) {
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		profile.setOpenMenu(Menu.DELETING_CHARACTER);
		Inventory inventory = getCharacterDeleteInventory(player);
		player.openInventory(inventory);
	}

	private void selectCharacter(Player player, PersistentPlayerCharacterDataContainer data) {
		player.sendMessage(ChatColor.YELLOW + "Logging in...");
		Inventory inventory = player.getInventory();
		inventory.clear();
		PlayerCharacter.registerPlayerCharacter(player, data);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		inventory.setItem(8, OPEN_MENU_ITEM_STACK);
	}

	private void removePlayerCharacter(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		PlayerCharacterSelectionProfile profile = profileMap.get(player);
		int characterSlot = profile.getCurrentCharacterSlot();
		PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createSaveData(pc);
		File saveFile = getCharacterSaveFile(player, characterSlot);
		IOUtils.writeJson(saveFile, data);
		pc.remove();
	}

	/*
	 * - select character menu - slot 1 - slot 2 - slot 3 - slot 4 - delete
	 * character - select player class menu - fighter - mage - back - delete
	 * character menu - slot 1 - slot 2 - slot 3 - slot 4 - back - delete confirm
	 * menu - confirm - cancel
	 * 
	 */

}
