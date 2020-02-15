package com.mcmmorpg.impl.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.Worlds;

public class MainMenuListener implements Listener {

	private static final File PLAYER_CHARACTER_DATA_DIRECTORY;
	private static final Location MAIN_MENU_LOCATION;
	private static final ItemStack SELECT_FIGHTER;
	private static final ItemStack SELECT_MAGE;
	private static final String STARTING_ZONE;
	private static final Location START_LOCATION;
	private static final ItemStack MENU;
	private static final ItemStack QUEST_LOG;
	private static final ItemStack SKILL_TREE;
	private static final ItemStack MAIN_MENU;

	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

	private static final Map<PlayerCharacter, Integer> slotMap = new HashMap<>();

	static {
		PLAYER_CHARACTER_DATA_DIRECTORY = new File(IOUtils.getDataFolder(), "playerSaveData");
		if (!PLAYER_CHARACTER_DATA_DIRECTORY.exists()) {
			PLAYER_CHARACTER_DATA_DIRECTORY.mkdir();
		}
		MAIN_MENU_LOCATION = new Location(Worlds.MAIN_MENU, 0, 64, 0);
		SELECT_FIGHTER = ItemFactory.createItemStack(ChatColor.GREEN + "Fighter", "Excels in close range combat",
				Material.IRON_SWORD);
		ItemFactory.registerStaticInteractable(SELECT_FIGHTER);
		SELECT_MAGE = ItemFactory.createItemStack(ChatColor.GREEN + "Mage", "Excels in ranged combat with magic",
				Material.NETHER_STAR);
		ItemFactory.registerStaticInteractable(SELECT_MAGE);
		STARTING_ZONE = "Melcher";
		START_LOCATION = new Location(Worlds.ELADRADOR, 0, 70, 0);
		MENU = ItemFactory.createItemStack(ChatColor.GREEN + "Menu", null, Material.BOOK);
		ItemFactory.registerStaticInteractable(MENU);
		QUEST_LOG = ItemFactory.createItemStack(ChatColor.GREEN + "Quest Log", null, Material.COMPASS);
		ItemFactory.registerStaticInteractable(QUEST_LOG);
		SKILL_TREE = ItemFactory.createItemStack(ChatColor.GREEN + "Skill Tree", null, Material.OAK_SAPLING);
		ItemFactory.registerStaticInteractable(SKILL_TREE);
		MAIN_MENU = ItemFactory.createItemStack(ChatColor.GREEN + "Main Menu", null, Material.EMERALD);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		player.teleport(MAIN_MENU_LOCATION);
		Inventory inventory = player.getInventory();
		inventory.clear();
		openMainMenu(player);
	}

	private void openMainMenu(Player player) {
		player.teleport(MAIN_MENU_LOCATION);
		PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
		player.addPotionEffect(invisibility);
		Inventory inventory = player.getInventory();
		inventory.clear();
		Inventory mainMenu = Bukkit.createInventory(player, 9, "Main Menu");
		for (int i = 0; i < 4; i++) {
			ItemStack characterItemStack = getCharacterSlotItemStack(player, i);
			mainMenu.setItem(i * 2 + 1, characterItemStack);
		}
		player.openInventory(mainMenu);
	}

	@EventHandler
	private void onMainMenuClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getWorld() == MAIN_MENU_LOCATION.getWorld()) {
			if (event.getView().getTitle().equals("Main Menu")) {
				// if we don't delay it, errors will arise
				new DelayedTask() {
					@Override
					protected void run() {
						//player.openInventory(event.getInventory());
					}
				}.schedule();
			}
		}
	}

	private ItemStack getCharacterSlotItemStack(Player player, int slot) {
		PersistentPlayerCharacterDataContainer data = getSaveDataFromFile(player, slot);
		ItemStack itemStack;
		if (data == null) {
			itemStack = getCreateNewCharacterItemStack(slot);
		} else {
			PlayerClass playerClass = data.getPlayerClass();
			String lore = ChatColor.GREEN + "Player Class: " + playerClass.getName() + ChatColor.GOLD + "\nLevel "
					+ PlayerCharacter.xpToLevel(data.getXP()) + ChatColor.GREEN + "Zone: " + data.getZone();
			itemStack = ItemFactory.createItemStack("Character Slot " + slot, lore,
					materialForPlayerClass(playerClass));
		}
		ItemFactory.registerStaticInteractable(itemStack);
		return itemStack;
	}

	private ItemStack getCreateNewCharacterItemStack(int slot) {
		return ItemFactory.createItemStack("Slot " + slot, "Create New Character", Material.GLASS);
	}

	private PersistentPlayerCharacterDataContainer getSaveDataFromFile(Player player, int slot) {
		File dataFile = getDataFile(player, slot);
		if (dataFile.exists()) {
			return IOUtils.objectFromJsonFile(dataFile, PersistentPlayerCharacterDataContainer.class);
		} else {
			return null;
		}
	}

	private Material materialForPlayerClass(PlayerClass playerClass) {
		if (playerClass.getName().equals("Fighter")) {
			return Material.IRON_SWORD;
		} else if (playerClass.getName().equals("Mage")) {
			return Material.STICK;
		} else {
			return null;
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			// the player is at the main menu
			unregisterMainMenuInteractables(player);
		} else {
			PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createSaveData(pc);
			int slot = slotMap.get(pc);
			savePlayerCharacterDataToFile(player, data, slot);
			pc.remove();
		}
	}

	private void unregisterMainMenuInteractables(Player player) {
		Inventory mainMenu = player.getOpenInventory().getTopInventory();
		ItemStack[] contents = mainMenu.getContents();
		for (ItemStack itemStack : contents) {
			if (itemStack != null) {
				ItemFactory.unregisterStaticInteractable(itemStack);
			}
		}
	}

	private File getDataFile(Player player, int slot) {
		File playerFolder = new File(PLAYER_CHARACTER_DATA_DIRECTORY, player.getName());
		File dataFile = new File(playerFolder, "saveSlot" + slot + ".json");
		return dataFile;
	}

	@EventHandler
	private void onSelectPlayerClass(StaticInteractableEvent event) {
		ItemStack interactable = event.getInteractable();
		PlayerClass playerClass;
		if (interactable.equals(SELECT_FIGHTER)) {
			playerClass = PlayerClass.forName("Fighter");
		} else if (interactable.equals(SELECT_MAGE)) {
			playerClass = PlayerClass.forName("Mage");
		} else {
			return;
		}
		Player player = event.getPlayer();
		InventoryView inventory = player.getOpenInventory();
		String title = inventory.getTitle();
		int slot = Integer.parseInt(String.valueOf(title.charAt(5)));
		player.sendMessage(ChatColor.GREEN + "Creating new character...");
		PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createFreshSaveData(player,
				playerClass, STARTING_ZONE, START_LOCATION);
		savePlayerCharacterDataToFile(player, data, slot);
	}

	@EventHandler
	private void onCreateNewCharacter(StaticInteractableEvent event) {
		ItemStack interactable = event.getInteractable();
		if (isCreateNewCharacterInteractable(interactable)) {
			Player player = event.getPlayer();
			Inventory inventory = player.getOpenInventory().getTopInventory();
			int slot = 0;
			for (int i = 0; i < 4; i++) {
				ItemStack itemStack = inventory.getItem(i * 2 + 1);
				if (itemStack == interactable) {
					break;
				}
				slot++;
			}
			openPlayerClassSelectionMenu(player, slot);
		}
	}

	private void openPlayerClassSelectionMenu(Player player, int slot) {
		Inventory menu = Bukkit.createInventory(player, 9, "Slot " + slot + ": Select Class");
		menu.setItem(2, SELECT_FIGHTER);
		menu.setItem(6, SELECT_MAGE);
		player.openInventory(menu);
	}

	@EventHandler
	private void onSelectExistingCharacter(StaticInteractableEvent event) {
		Player player = event.getPlayer();
		InventoryView inventory = player.getOpenInventory();
		ItemStack interactable = event.getInteractable();
		String interactableName = interactable.getItemMeta().getDisplayName();
		if (!isCreateNewCharacterInteractable(interactable)) {
			player.sendMessage(ChatColor.GREEN + "Logging in...");
			int slot = interactableName.charAt(5);
			unregisterMainMenuInteractables(player);
			File dataFile = getDataFile(player, slot);
			PersistentPlayerCharacterDataContainer data = IOUtils.objectFromJsonFile(dataFile,
					PersistentPlayerCharacterDataContainer.class);
			loadCharacterFromData(player, data, slot);
		}
	}

	private boolean isCreateNewCharacterInteractable(ItemStack interactable) {
		ItemMeta itemMeta = interactable.getItemMeta();
		List<String> lore = itemMeta.getLore();
		String line0 = lore.get(0);
		Debug.log(line0.contains("Create New"));
		return line0.contains("Create New");
	}

	private void loadCharacterFromData(Player player, PersistentPlayerCharacterDataContainer data, int slot) {
		Inventory inventory = player.getInventory();
		inventory.clear();
		PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, data);
		slotMap.put(pc, slot);
		inventory.setItem(8, MENU);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	private void savePlayerCharacterDataToFile(Player player, PersistentPlayerCharacterDataContainer data, int slot) {
		File dataFile = getDataFile(player, slot);
		IOUtils.objectToJsonFile(dataFile, data);
	}

	@EventHandler
	private void onOpenMenu(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(MENU)) {
			Player player = event.getPlayer();
			Inventory menu = Bukkit.createInventory(null, 9, "Menu");
			menu.setItem(2, SKILL_TREE);
			menu.setItem(6, QUEST_LOG);
			player.openInventory(menu);
			CLICK_NOISE.play(player);
		}
	}

	@EventHandler
	private void onOpenQuestLog(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(QUEST_LOG)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			pc.openQuestLog();
			CLICK_NOISE.play(player);
		}
	}

	@EventHandler
	private void onOpenSkillTree(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(SKILL_TREE)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			pc.getPlayerClass().getSkillTree().open(pc);
			CLICK_NOISE.play(player);
		}
	}

	@EventHandler
	private void onReturnToMainMenu(StaticInteractableEvent event) {
		ItemStack itemStack = event.getInteractable();
		if (itemStack.equals(MAIN_MENU)) {
			Player player = event.getPlayer();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			PersistentPlayerCharacterDataContainer data = PersistentPlayerCharacterDataContainer.createSaveData(pc);
			int slot = slotMap.get(pc);
			savePlayerCharacterDataToFile(player, data, slot);
			pc.remove();
			slotMap.remove(pc);
			openMainMenu(player);
		}
	}

}
