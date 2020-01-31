package com.mcmmorpg.common.sound;

import org.bukkit.entity.Player;

public class PlayerSoundtrackPlayer {

	private final Player player;
	private NoiseSequencePlayer noisePlayer;

	public PlayerSoundtrackPlayer(Player player) {
		this.player = player;
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
			noisePlayer = new NoiseSequencePlayer(soundtrack, player);
			noisePlayer.setLooping(true);
			noisePlayer.play();
		}
	}

}
