package com.mcmmorpg.test;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.NoiseSequence;
import com.mcmmorpg.common.sound.NoiseSequencePlayer;
import com.mcmmorpg.common.time.DelayedTask;

public class TestPlugin extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

		NoiseSequence sequence = new NoiseSequence(10);
		sequence.add(new Noise(Sound.BLOCK_ANVIL_BREAK), 2);
		sequence.add(new Noise(Sound.ENTITY_ZOMBIE_AMBIENT), 4);
		sequence.add(new Noise(Sound.ENTITY_VILLAGER_AMBIENT), 8);

		Player player = Debug.getFirstPlayer();
		if (player != null) {
			NoiseSequencePlayer sequencePlayer = new NoiseSequencePlayer(sequence, player);
			sequencePlayer.setLooping(true);
			sequencePlayer.play();

			DelayedTask stop = new DelayedTask(5) {
				@Override
				public void run() {
					sequencePlayer.pause();
				}
			};
			stop.schedule();

			DelayedTask play = new DelayedTask(3) {
				@Override
				public void run() {
					sequencePlayer.play();
				}
			};
			play.schedule();
		}
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

}
