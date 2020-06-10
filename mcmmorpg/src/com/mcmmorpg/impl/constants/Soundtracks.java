package com.mcmmorpg.impl.constants;

import java.io.File;

import com.mcmmorpg.common.sound.PersistentSoundSequenceDataContainer;
import com.mcmmorpg.common.sound.SoundSequence;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Contains references to all soundtracks that are loaded from the plugin's data
 * folder for convenience.
 */
public class Soundtracks {

	/**
	 * The soundtrack to play when player characters are in villages.
	 */
	public static final SoundSequence VILLAGE;
	/**
	 * The soundtrack to play when player characters are in the wilderness.
	 */
	public static final SoundSequence WILDNERNESS;
	/**
	 * The soundtrack to play when player characters are in dungeons.
	 */
	public static final SoundSequence DUNGEON;

	/*
	 * Load soundtracks from the plugin's data folder.
	 */
	static {
		File soundtrackFolder = new File(IOUtility.getDataFolder(), "resources/soundtracks");

		File villageFile = new File(soundtrackFolder, "Village.json");
		VILLAGE = IOUtility.readJson(villageFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();

		File wildernessFile = new File(soundtrackFolder, "Wilderness.json");
		WILDNERNESS = IOUtility.readJson(wildernessFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();

		File dungeonFile = new File(soundtrackFolder, "Dungeon.json");
		DUNGEON = IOUtility.readJson(dungeonFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();
	}

}
