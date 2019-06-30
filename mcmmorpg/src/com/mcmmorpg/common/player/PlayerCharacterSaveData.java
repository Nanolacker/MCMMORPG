package com.mcmmorpg.common.player;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestStatus;

public class PlayerCharacterSaveData implements Serializable {

	private final String name;

	// location
	private final String worldName;
	private final double locX, locY, locZ;
	private final float locYaw, locPitch;

	private final String playerClassName;
	private final int xp;
	private final QuestStatus[] questStatuses;

	private PlayerCharacterSaveData(String name, Location location, PlayerClass playerClass, int xp,
			QuestStatus[] questStatuses) {
		this.name = name;
		worldName = location.getWorld().getName();
		locX = location.getX();
		locY = location.getY();
		locZ = location.getZ();
		locYaw = location.getYaw();
		locPitch = location.getPitch();
		playerClassName = playerClass.getName();
		this.xp = xp;
		this.questStatuses = questStatuses;
	}

	public static PlayerCharacterSaveData getSave(PlayerCharacter pc) {
		String name = pc.getName();
		Location location = pc.getLocation();
		PlayerClass playerClass = pc.getPlayerClass();
		int xp = pc.getXP();
		QuestStatus[] questStatuses = pc.getQuestStatusManager().getQuestStatuses();
		return new PlayerCharacterSaveData(name, location, playerClass, xp, questStatuses);
	}

	public static PlayerCharacterSaveData getFreshSave(Player player, PlayerClass playerClass,
			Location startingLocation) {
		String name = player.getName();
		int xp = 0;
		QuestStatus[] questStatuses = {};
		return new PlayerCharacterSaveData(name, startingLocation, playerClass, xp, questStatuses);
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		World world = Bukkit.getWorld(worldName);
		return new Location(world, locX, locY, locZ, locYaw, locPitch);
	}

	public PlayerClass getPlayerClass() {
		return PlayerClass.forName(playerClassName);
	}

	public int getXP() {
		return xp;
	}

	public QuestStatus[] getQuestStatuses() {
		return questStatuses;
	}

}
