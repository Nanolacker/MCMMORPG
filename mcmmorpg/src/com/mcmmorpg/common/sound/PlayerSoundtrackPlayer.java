package com.mcmmorpg.common.sound;

import com.mcmmorpg.common.character.PlayerCharacter;

public class PlayerSoundtrackPlayer {

	private final PlayerCharacter pc;
	private NoiseSequencePlayer noisePlayer;

	public PlayerSoundtrackPlayer(PlayerCharacter pc) {
		this.pc = pc;
	}

	/**
	 * 
	 * @param soundtrack null to stop playing
	 */
	public void setSoundtrack(NoiseSequence soundtrack) {
		if (soundtrack == null) {
			noisePlayer.stop();
			noisePlayer = null;
		} else {
			noisePlayer = new NoiseSequencePlayer(soundtrack, pc.getPlayer());
			noisePlayer.setLooping(true);
			noisePlayer.play();
		}
	}

}
