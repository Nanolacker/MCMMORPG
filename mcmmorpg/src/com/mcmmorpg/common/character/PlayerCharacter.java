package com.mcmmorpg.common.character;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.player.PlayerCharacterSaveData;
import com.mcmmorpg.common.player.PlayerClass;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.quest.QuestStatusManager;

public class PlayerCharacter extends CommonCharacter {

	private static Map<Player, PlayerCharacter> playerMap;

	private final PlayerClass playerClass;
	private double currentMana;
	private double maxMana;
	private int xp;
	private final QuestStatusManager questStatusManager;

	static {
		playerMap = new HashMap<>();
	}

	private PlayerCharacter(Player player, String name, int level, Location location, PlayerClass playerClass,
			QuestStatus[] questStatuses) {
		super(name, level, location);
		this.playerClass = playerClass;
		questStatusManager = new QuestStatusManager(questStatuses);
		playerMap.put(player, this);
	}

	public PlayerCharacter fromSaveData(Player player, PlayerCharacterSaveData saveData) {
		String name = saveData.getName();
		int level = 0;
		Location location = saveData.getLocation();
		PlayerClass playerClass = saveData.getPlayerClass();
		QuestStatus[] questStatuses = saveData.getQuestStatuses();
		return new PlayerCharacter(player, name, level, location, playerClass, questStatuses);
	}

	public static PlayerCharacter forPlayer(Player player) {
		return playerMap.get(player);
	}

	public void bindPlayer() {

	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public QuestStatusManager getQuestStatusManager() {
		return questStatusManager;
	}

	public int getXP() {
		return xp;
	}

	public void grantXP(int xp) {
		this.xp += xp;
	}

}
