package com.mcmmorpg.common.sound;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.sound.NoiseSequence.NoiseSequenceNode;
import com.mcmmorpg.common.time.DelayedTask;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class NoiseSequencePlayer {

	private final NoiseSequence sequence;

	public NoiseSequencePlayer(NoiseSequence sequence) {
		this.sequence = sequence;
	}

	public void play(Location source) {
		Consumer<Noise> consumer = e -> e.play(source);
		play(consumer);
	}

	public void play(Player player) {
		Consumer<Noise> consumer = e -> e.play(player);
		play(consumer);
	}

	public void play(Player player, Location source) {
		Consumer<Noise> consumer = e -> e.play(player, source);
		play(consumer);
	}

	private void play(Consumer<Noise> consumer) {
		List<NoiseSequenceNode> nodes = sequence.getNodes();
		for (NoiseSequenceNode node : nodes) {
			Noise noise = node.getNoise();
			double timeSeconds = node.getTimeSeconds();
			DelayedTask play = new DelayedTask(timeSeconds) {
				@Override
				protected void run() {
					consumer.accept(noise);
				}
			};
			play.schedule();
		}
	}

}
