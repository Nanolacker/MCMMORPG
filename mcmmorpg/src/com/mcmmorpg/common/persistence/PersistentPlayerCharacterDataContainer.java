package com.mcmmorpg.common.persistence;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.PlayerSkillData;
import com.mcmmorpg.common.quest.PlayerQuestData;
import com.mcmmorpg.common.quest.PlayerQuestManager;
import com.mcmmorpg.common.quest.Quest;

public class PersistentPlayerCharacterDataContainer {

	private final boolean fresh;
	private final String playerClassName;
	private final String zone;
	private final PersistentLocation location, respawnLocation;
	private final int xp;
	private final int skillUpgradePoints;
	private final int currency;
	private final double maxHealth, currentHealth;
	private final double maxMana, currentMana;
	private final String targetQuestName;
	private final String[] completedQuestNames;
	private final PlayerQuestData[] questData;
	private final PlayerSkillData[] skillData;
	private final PersistentInventory inventory;

	private PersistentPlayerCharacterDataContainer(boolean fresh, PlayerClass playerClass, String zone,
			Location location, Location respawnLocation, int xp, int skillUpgradePoints, int currency, double maxHealth,
			double currentHealth, double maxMana, double currentMana, Quest targetQuest, List<Quest> completedQuests,
			PlayerQuestData[] questData, PlayerSkillData[] skillData, ItemStack[] inventoryContents) {
		this.fresh = fresh;
		this.playerClassName = playerClass.getName();
		this.zone = zone;
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
		this.skillData = skillData;
		this.inventory = new PersistentInventory(inventoryContents);
	}

	public static PersistentPlayerCharacterDataContainer createSaveData(PlayerCharacter pc) {
		PlayerClass playerClass = pc.getPlayerClass();
		String zone = pc.getZone();
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
		PlayerSkillData[] skillData = pc.getSkillManager().getAllSkillData();
		ItemStack[] inventoryContents = pc.getInventory().getContents();
		return new PersistentPlayerCharacterDataContainer(false, playerClass, zone, location, respawnLocation, xp,
				skillUpgradePoints, currency, maxHealth, currentHealth, maxMana, currentMana, targetQuest,
				completedQuests, allQuestData, skillData, inventoryContents);
	}

	public static PersistentPlayerCharacterDataContainer createFreshSaveData(Player player, PlayerClass playerClass,
			String startZone, Location startLocation) {
		Location respawnLocation = startLocation;
		int xp = 0;
		int skillUpgradePoints = 0;
		int currency = 0;
		double maxHealth = 20; // CHANGE THIS LATER!!!
		double currentHealth = maxHealth;
		double maxMana = 20;
		double currentMana = maxMana;

		// These are handled after the player logs in for the first time.
		PlayerQuestData[] questData = {};
		PlayerSkillData[] skillData = {};

		ItemStack[] inventoryContents = {};
		return new PersistentPlayerCharacterDataContainer(true, playerClass, startZone, startLocation, respawnLocation,
				xp, skillUpgradePoints, currency, maxHealth, currentHealth, maxMana, currentMana, null,
				new ArrayList<>(), questData, skillData, inventoryContents);
	}

	/**
	 * Returns whether this data has been used before.
	 */
	public boolean isFresh() {
		return fresh;
	}

	public PlayerClass getPlayerClass() {
		return PlayerClass.forName(playerClassName);
	}

	public String getZone() {
		return zone;
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

	public PlayerSkillData[] getSkillData() {
		return skillData;
	}

	public ItemStack[] getInventoryContents() {
		return inventory.getContents();
	}

}
