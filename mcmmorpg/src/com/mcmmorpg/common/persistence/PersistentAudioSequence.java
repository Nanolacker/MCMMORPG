package com.mcmmorpg.common.persistence;

import com.mcmmorpg.common.audio.AudioSequence;

/**
 * This can be used to store audio sequences in a file. example: { "duration":
 * "5", "sounds":[ { "time":"1", "type":"BLOCK_NOTE_BLOCK_IRON_XYLOPHONE",
 * "volume": "1", "pitch": "C_7" }, { "time":"3",
 * "type":"BLOCK_NOTE_BLOCK_IRON_XYLOPHONE", "volume": "1", "pitch": "1.12" } ]
 * }
 */
public class PersistentAudioSequence {

	private String duration;
	private PersistentAudioSource[] sounds;

	public AudioSequence toSoundSequence() {
		AudioSequence sequence = new AudioSequence(Double.parseDouble(duration));
		for (PersistentAudioSource sound : sounds) {
			sequence.add(sound.toAudioSource(), sound.getTime());
		}
		return sequence;
	}

}
