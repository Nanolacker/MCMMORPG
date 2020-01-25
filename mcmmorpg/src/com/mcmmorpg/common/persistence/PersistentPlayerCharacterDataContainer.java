package com.mcmmorpg.common.persistence;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillData;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;

public class PersistentPlayerCharacterDataContainer {

	private final String playerClassName;
	private final PersistentLocation location, respawnLocation;
	private final int xp;
	private final int skillUpgradePoints;
	private final int currency;
	private final double maxHealth, currentHealth;
	private final double maxMana, currentMana;
	private final String targetQuestName;
	private final String[] completedQuestNames;
	private final PlayerQuestData[] questData;
	private final PlayerSkillData[] skillStatuses;
	private final PersistentInventory inventory;

	private PersistentPlayerCharacterDataContainer(PlayerClass playerClass, Location location, Location respawnLocation, int xp,
			int skillUpgradePoints, int currency, double maxHealth, double currentHealth, double maxMana,
			double currentMana, Quest targetQuest, List<Quest> completedQuests, PlayerQuestData[] questData,
			PlayerSkillData[] skillStatuses, Inventory inventory) {
		this.playerClassName = playerClass.getName();
		this.location = new PersistentLocation(location);
		this.respawnLocation = new PersistentLocation(respawnLocation);
		this.xp = xp;
		this.skillUpgradePoints = skillUpgradePoints;
		this.currency = currency;
		this.maxHealth = maxHealth;
		this.currentHealth = currentHealth;
		this.maxMana = maxMana;
		this.currentMana = currentMana;
		this.targetQuestName = targetQuest == null ? null : targetQuest.getName();
		this.completedQuestNames = new String[completedQuests.size()];
		for (int i = 0; i < completedQuests.size(); i++) {
			completedQuestNames[i] = completedQuests.get(i).getName();
		}
		this.questData = questData;
		this.skillStatuses = skillStatuses;
		this.inventory = new PersistentInventory(inventory);
	}

	public static PersistentPlayerCharacterDataContainer createSaveData(PlayerCharacter pc) {
		PlayerClass playerClass = pc.getPlayerClass();
		Location location = pc.getLocation();
		Location respawnLocation = pc.getRespawnLocation();
		int xp = pc.getXP();
		int skillUpgradePoints = pc.getSkillUpgradePoints();
		int currency = pc.getCurrency();
		double maxHealth = pc.getMaxHealth();
		double currentHealth = pc.getCurrentHealth();
		double maxMana = pc.getMaxMana();
		double currentMana = pc.getCurrentMana();
		Quest targetQuest = pc.getTargetQuest();
		PlayerQuestManager questManager = pc.getQuestManager();
		List<Quest> completedQuests = questManager.getCompletedQuests();
		PlayerQuestData[] allQuestData = pc.getQuestManager().getQuestData();
		PlayerSkillData[] skillStatuses = pc.getSkillManager().getAllSkillData();
		Inventory inventory = pc.getInventory();
		return new PersistentPlayerCharacterDataContainer(playerClass, location, respawnLocation, xp, skillUpgradePoints, currency,
				maxHealth, currentHealth, maxMana, currentMana, targetQuest, completedQuests, allQuestData,
				skillStatuses, inventory);
	}

	public static PersistentPlayerCharacterDataContainer createFreshSaveData(Player player, PlayerClass playerClass,
			Location startLocation) {
		Location respawnLocation = startLocation;
		int xp = 0;
		int skillUpgradePoints = 0;
		int currency = 0;
		double maxHealth = 20; // CHANGE THIS LATER!!!
		double currentHealth = maxHealth;
		double maxMana = 20;
		double currentMana = maxMana;
		PlayerQuestData[] questData = {};
		PlayerSkillData[] skillStatuses = {}; // TODO: MAKE SURE THIS ALLIGNS WITH PLAYER CLASS!!!
		Inventory inventory = player.getInventory();
		return new PersistentPlayerCharacterDataContainer(playerClass, startLocation, respawnLocation, xp, skillUpgradePoints,
				currency, maxHealth, currentHealth, maxMana, currentMana, null, new ArrayList<>(), questData,
				skillStatuses, inventory);
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

	public int getSkillUpgradePoints() {
		return skillUpgradePoints;
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

	public PlayerSkillData[] getSkillStatuses() {
		return skillStatuses;
	}

	public ItemStack[] getInventoryContents() {
		return inventory.getContents();
	}

}
