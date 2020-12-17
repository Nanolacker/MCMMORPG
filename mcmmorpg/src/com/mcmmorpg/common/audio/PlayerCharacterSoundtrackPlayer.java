package com.mcmmorpg.common.audio;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * Manages the music played to a player character. Only plays one soundtrack at
 * a time.
 */
public class PlayerCharacterSoundtrackPlayer {

	private final PlayerCharacter pc;
	private AudioSequencePlayer audioSequencePlayer;

	/**
	 * Create a new soundtrack player for the specified player character.
	 */
	public PlayerCharacterSoundtrackPlayer(PlayerCharacter pc) {
		this.pc = pc;
	}

	/**
	 * Set the soundtrack to play to the player character, or null to stop playing
	 * music.
	 */
	public void setSoundtrack(AudioSequence soundtrack) {
		if (audioSequencePlayer != null && soundtrack != audioSequencePlayer.getAudioSequence()) {
			audioSequencePlayer.stop();
		}
		if (soundtrack == null) {
			audioSequencePlayer = null;
		} else if (audioSequencePlayer != null && soundtrack == audioSequencePlayer.getAudioSequence()) {
			return;
		} else {
			audioSequencePlayer = new AudioSequencePlayer(soundtrack, pc);
			audioSequencePlayer.setLooping(true);
			audioSequencePlayer.play();
		}
	}

}
