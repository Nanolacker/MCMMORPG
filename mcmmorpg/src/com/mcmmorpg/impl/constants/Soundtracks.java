package com.mcmmorpg.impl.constants;

import java.io.File;

import com.mcmmorpg.common.sound.PersistentSoundSequenceDataContainer;
import com.mcmmorpg.common.sound.SoundSequence;
import com.mcmmorpg.common.util.IOUtility;

/**
 * Provides easy access to the soundtracks of the game.
 */
public class Soundtracks {

	public static final SoundSequence VILLAGE;
	public static final SoundSequence WILDNERNESS;
	public static final SoundSequence DUNGEON;

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
