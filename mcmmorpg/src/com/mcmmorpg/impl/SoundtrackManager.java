package com.mcmmorpg.impl;

import java.io.File;

import com.mcmmorpg.common.sound.PersistentSoundSequenceDataContainer;
import com.mcmmorpg.common.sound.SoundSequence;
import com.mcmmorpg.common.utils.IOUtils;

/**
 * Provides easy access to the soundtracks of the game.
 */
public class SoundtrackManager {

	public static final SoundSequence VILLAGE;
	public static final SoundSequence WILDNERNESS;
	public static final SoundSequence DUNGEON;

	static {
		File soundtrackFolder = new File(IOUtils.getDataFolder(), "resources\\soundtracks");

		File villageFile = new File(soundtrackFolder, "Village.json");
		VILLAGE = IOUtils.readJson(villageFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();

		File wildernessFile = new File(soundtrackFolder, "Wilderness.json");
		WILDNERNESS = IOUtils.readJson(wildernessFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();

		File dungeonFile = new File(soundtrackFolder, "Dungeon.json");
		DUNGEON = IOUtils.readJson(dungeonFile, PersistentSoundSequenceDataContainer.class).toSoundSequence();
	}

}
