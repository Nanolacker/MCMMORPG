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
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.QuestCompletionEvent;
import com.mcmmorpg.common.item.ItemFactory;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterDataContainer;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.utils.Debug;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.npcs.BulskanUndead;

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

		menuItem = ItemFactory.createItemStack0("Menu", null, Material.EMERALD);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = getSaveFile(player.getName());
		PersistentPlayerCharacterDataContainer saveData;
		if (file.exists()) {
			// load data from file
			Debug.log("loading character from file");
			saveData = IOUtils.objectFromJsonFile(file, PersistentPlayerCharacterDataContainer.class);
		} else {
			// create new data
			Debug.log("creating new character");
			PlayerClass playerClass = PlayerClass.forName("Fighter");
			saveData = PersistentPlayerCharacterDataContainer.createFreshSaveData(player, playerClass, "Starting Zone", startingLocation);
		}
		PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
		Quest quest = Quest.forName("Saving the Farm");
		if (quest.getStatus(pc) == QuestStatus.NOT_STARTED) {
			quest.start(pc);
		}
		pc.setTargetQuest(Quest.forName("Saving the Farm"));

		BulskanUndead undead = new BulskanUndead(1, pc.getLocation(), 5);
		undead.setAlive(true);
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
		PersistentPlayerCharacterDataContainer saveData = PersistentPlayerCharacterDataContainer.createSaveData(pc);
		IOUtils.objectToJsonFile(file, saveData);
		pc.deactivate();
	}

	private File getSaveFile(String playerName) {
		File saveFile = new File(saveDataDirectory, playerName + ".json");
		return saveFile;
	}

	@EventHandler
	private void onRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		Vector lookDir = loc.getDirection();
		@SuppressWarnings("deprecation")
		Raycast raycast = new Raycast(loc, lookDir, 10);
		Collider[] colliders = raycast.getHits();
		for (Collider collider : colliders) {
			if (collider instanceof CharacterCollider) {
				AbstractCharacter character = ((CharacterCollider) collider).getCharacter();
				character.setCurrentHealth(character.getCurrentHealth() - 5);
			}
		}

//		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
//		if (pc == null) {
//			return;
//		}
//		PlayerClass playerClass = pc.getPlayerClass();
//		SkillTree tree = playerClass.getSkillTree();
//		tree.open(pc);
	}

	@EventHandler
	private void onCompleteQuest(QuestCompletionEvent event) {
		event.getPlayer().sendMessage("You did it!");
	}

}
