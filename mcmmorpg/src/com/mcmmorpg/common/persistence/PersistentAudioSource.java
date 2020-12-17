package com.mcmmorpg.common.persistence;

import org.bukkit.Sound;

import com.mcmmorpg.common.audio.AudioSource;
import com.mcmmorpg.common.audio.MusicNote;
import com.mcmmorpg.common.util.StringUtility;

public class PersistentAudioSource {

	private String time;
	private String type;
	private String volume;
	private String pitch;

	public AudioSource toAudioSource() {
		return new AudioSource(getType(), getVolume(), getPitch());
	}

	public double getTime() {
		return Double.parseDouble(time);
	}

	public Sound getType() {
		return Sound.valueOf(type);
	}

	public float getVolume() {
		return Float.parseFloat(volume);
	}

	public float getPitch() {
		if (StringUtility.isNumeric(pitch)) {
			return Float.parseFloat(pitch);
		} else {
			return MusicNote.valueOf(pitch).getPitch();
		}
	}
}
