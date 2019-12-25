package com.mcmmorpg.common.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillStatus;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;

public class PlayerCharacterSaveData {

	private final String playerClassName;
	private final PersistentLocation location, respawnLocation;
	private final int xp;
	private final int currency;
	private final double maxHealth, currentHealth;
	private final double maxMana, currentMana;
	private final String targetQuestName;
	private final String[] completedQuestNames;
	private final PlayerQuestData[] questData;
	private final PlayerSkillStatus[] skillStatuses;
	private final PersistentInventory inventory;

	private PlayerCharacterSaveData(PlayerClass playerClass, Location location, Location respawnLocation, int xp,
			int currency, double maxHealth, double currentHealth, double maxMana, double currentMana, Quest targetQuest,
			List<Quest> completedQuests, PlayerQuestData[] questStatuses, PlayerSkillStatus[] skillStatuses,
			Inventory inventory) {
		this.playerClassName = playerClass.getName();
		this.location = new PersistentLocation(location);
		this.respawnLocation = new PersistentLocation(respawnLocation);
		this.xp = xp;
		this.currency = currency;
		this.maxHealth = maxHealth;
		this.currentHealth = currentHealth;
		this.maxMana = maxMana;
		this.currentMana = currentMana;
		this.targetQuestName = targetQuest.getName();
		this.completedQuestNames = new String[completedQuests.size()];
		for (int i = 0; i < completedQuests.size(); i++) {
			completedQuestNames[i] = completedQuests.get(i).getName();
		}
		this.questData = questStatuses;
		this.skillStatuses = skillStatuses;
		this.inventory = new PersistentInventory(inventory);
	}

	public static PlayerCharacterSaveData createSaveData(PlayerCharacter pc) {
		PlayerClass playerClass = pc.getPlayerClass();
		Location location = pc.getLocation();
		Location respawnLocation = pc.getRespawnLocation();
		int xp = pc.getXP();
		int currency = pc.getCurrency();
		double maxHealth = pc.getMaxHealth();
		double currentHealth = pc.getCurrentHealth();
		double maxMana = pc.getMaxMana();
		double currentMana = pc.getCurrentMana();
		Quest targetQuest = pc.getTargetQuest();
		PlayerQuestManager questManager = pc.getQuestManager();
		List<Quest> completedQuests = questManager.getCompletedQuests();
		PlayerQuestData[] questStatuses = pc.getQuestManager().getAllQuestData();
		PlayerSkillStatus[] skillStatuses = pc.getSkillStatusManager().getSkillStatuses();
		Inventory inventory = pc.getInventory();
		return new PlayerCharacterSaveData(playerClass, location, respawnLocation, xp, currency, maxHealth,
				currentHealth, maxMana, currentMana, targetQuest, completedQuests, questStatuses, skillStatuses,
				inventory);
	}

	public static PlayerCharacterSaveData createFreshSaveData(Player player, PlayerClass playerClass,
			Location startingLocation) {
		Location respawnLocation = startingLocation;
		int xp = 0;
		int currency = 0;
		double maxHealth = 20; // CHANGE THIS LATER!!!
		double currentHealth = maxHealth;
		double maxMana = 20;
		double currentMana = maxMana;
		PlayerQuestData[] questStatuses = {};
		PlayerSkillStatus[] skillStatuses = {}; // TODO: MAKE SURE THIS ALLIGNS WITH PLAYER CLASS!!!
		Inventory inventory = player.getInventory();
		return new PlayerCharacterSaveData(playerClass, startingLocation, respawnLocation, xp, currency, maxHealth,
				currentHealth, maxMana, currentMana, null, new ArrayList<>(), questStatuses, skillStatuses, inventory);
	}

	public PlayerClass getPlayerClass() {
		return PlayerClass.forName(playerClassName);
	}

	public Location getLocation() {
		return location.toLocation();
	}

	public Location getRespawnLocation() {
		return respawnLocation.toLocation();
	}

	public int getXP() {
		return xp;
	}

	public int getCurrency() {
		return currency;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public double getCurrentHealth() {
		return currentHealth;
	}

	public double getMaxMana() {
		return maxMana;
	}

	public double getCurrentMana() {
		return currentMana;
	}

	public Quest getTargetQuest() {
		return Quest.forName(targetQuestName);
	}

	public Quest[] getCompletedQuests() {
		Quest[] quests = new Quest[completedQuestNames.length];
		for (int i = 0; i < completedQuestNames.length; i++) {
			quests[i] = Quest.forName(completedQuestNames[i]);
		}
		return quests;
	}

	public PlayerQuestData[] getQuestData() {
		return questData;
	}

	public PlayerSkillStatus[] getSkillStatuses() {
		return skillStatuses;
	}

	public ItemStack[] getInventoryContents() {
		return inventory.getContents();
	}

}
