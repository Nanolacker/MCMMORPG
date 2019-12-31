package com.mcmmorpg.test;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.QuestCompletionEvent;
import com.mcmmorpg.common.item.ItemStackFactory;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.SkillTree;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;

public class PCListener implements Listener {

	private final File saveDataDirectory;
	private final Location startingLocation;
	private final ItemStack menuItem;

	public PCListener() {
		File dataFolder = IOUtils.getDataFolder();
		saveDataDirectory = new File(dataFolder, "player_save_data");
		if (!saveDataDirectory.exists()) {
			saveDataDirectory.mkdir();
		}
		World world = Bukkit.getWorld("world");
		startingLocation = new Location(world, 141, 70, 66);

		menuItem = ItemStackFactory.create0("Menu", null, Material.EMERALD);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = getSaveFile(player.getName());
		PlayerCharacterSaveData saveData;
		if (file.exists()) {
			// load data from file
			Debug.log("loading character from file");
			saveData = IOUtils.jsonFromFile(file, PlayerCharacterSaveData.class);
		} else {
			// create new data
			Debug.log("creating new character");
			PlayerClass playerClass = PlayerClass.forName("Fighter");
			saveData = PlayerCharacterSaveData.createFreshSaveData(player, playerClass, startingLocation);
		}
		PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
		Quest quest = Quest.forName("Saving the Farm");
		quest.start(pc);
		pc.setTargetQuest(Quest.forName("Saving the Farm"));
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Debug.log("saving character");
		File file = getSaveFile(player.getName());
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createSaveData(pc);
		IOUtils.jsonToFile(file, saveData);
		pc.deactivate();
	}

	private File getSaveFile(String playerName) {
		File saveFile = new File(saveDataDirectory, playerName + ".json");
		return saveFile;
	}

	@EventHandler
	private void onRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		PlayerClass clazz = pc.getPlayerClass();
		SkillTree tree = clazz.getSkillTree();
		tree.open(pc);
	}

	@EventHandler
	private void onCompleteQuest(QuestCompletionEvent event) {
		event.getPlayer().sendMessage("You did it!");
	}

}
