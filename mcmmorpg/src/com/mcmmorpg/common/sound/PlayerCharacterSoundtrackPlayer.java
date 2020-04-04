package com.mcmmorpg.common.sound;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerCharacterSoundtrackPlayer {

	private final PlayerCharacter pc;
	private SoundSequencePlayer noisePlayer;

	public PlayerCharacterSoundtrackPlayer(PlayerCharacter pc) {
		this.pc = pc;
	}

	/**
	 * @param soundtrack null to stop playing
	 */
	public void setSoundtrack(SoundSequence soundtrack) {
		if (noisePlayer != null) {
			noisePlayer.stop();
		}
		if (soundtrack == null) {
			noisePlayer = null;
		} else if (soundtrack == noisePlayer.getSequence()) {
			return;
		} else {
			noisePlayer = new SoundSequencePlayer(soundtrack, pc);
			noisePlayer.setLooping(true);
			noisePlayer.play();
		}
	}

}
