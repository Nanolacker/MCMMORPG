package com.mcmmorpg.common.sound;

import org.bukkit.entity.Player;

public class PlayerSoundtrackPlayer {

	private final Player player;
	private SoundSequencePlayer noisePlayer;

	public PlayerSoundtrackPlayer(Player player) {
		this.player = player;
	}

	/**
	 * 
	 * @param soundtrack null to stop playing
	 */
	public void setSoundtrack(SoundSequence soundtrack) {
		if (noisePlayer != null) {
			noisePlayer.stop();
		}
		if (soundtrack == null) {
			noisePlayer = null;
		} else {
			noisePlayer = new SoundSequencePlayer(soundtrack, player);
			noisePlayer.setLooping(true);
			noisePlayer.play();
		}
	}

}
