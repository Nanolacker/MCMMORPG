package com.mcmmorpg.common.sound;

import org.bukkit.Sound;

import com.mcmmorpg.common.util.StringUtility;

/**
 * This can be used to store sound sequences in a file. Refer to the example
 * below to see how to create persistent sound sequences using JSON.
 */
public class PersistentSoundSequenceDataContainer {

	private String duration;
	private PersistentNoiseDataContainer[] sounds;

	public SoundSequence toSoundSequence() {
		SoundSequence sequence = new SoundSequence(Double.parseDouble(duration));
		for (PersistentNoiseDataContainer sound : sounds) {
			sequence.add(sound.toNoise(), sound.getTime());
		}
		return sequence;
	}

	private static class PersistentNoiseDataContainer {
		private String time;
		private String type;
		private String volume;
		private String pitch;

		private Noise toNoise() {
			return new Noise(getType(), getVolume(), getPitch());
		}

		private double getTime() {
			return Double.parseDouble(time);
		}

		private Sound getType() {
			return Sound.valueOf(type);
		}

		private float getVolume() {
			return Float.parseFloat(volume);
		}

		private float getPitch() {
			if (StringUtility.isNumeric(pitch)) {
				return Float.parseFloat(pitch);
			} else {
				return MusicNote.valueOf(pitch).getPitch();
			}
		}
	}

}

/*
 * example: { "duration": "5", "sounds":[ { "time":"1",
 * "type":"BLOCK_NOTE_BLOCK_IRON_XYLOPHONE", "volume": "1", "pitch": "C_7" }, {
 * "time":"3", "type":"BLOCK_NOTE_BLOCK_IRON_XYLOPHONE", "volume": "1", "pitch":
 * "1.12" } ] }
 */
