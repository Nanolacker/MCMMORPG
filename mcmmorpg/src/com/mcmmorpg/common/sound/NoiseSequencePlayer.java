package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.sound.NoiseSequence.NoiseSequenceNode;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.GameClock;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class NoiseSequencePlayer {

	private final NoiseSequence sequence;
	private final Consumer<Noise> noisePlayer;
	private final List<DelayedTask> playTasks;
	private boolean loop;
	private boolean isPlaying;
	/**
	 * In seconds.
	 */
	private double timeStamp;
	/**
	 * In seconds.
	 */
	private double startTime;

	public NoiseSequencePlayer(NoiseSequence sequence, Player player) {
		this(sequence, noise -> noise.play(player));
	}

	public NoiseSequencePlayer(NoiseSequence sequence, Player player, Location source) {
		this(sequence, noise -> noise.play(player, source));
	}

	public NoiseSequencePlayer(NoiseSequence sequence, Location source) {
		this(sequence, noise -> noise.play(source));
	}

	private NoiseSequencePlayer(NoiseSequence sequence, Consumer<Noise> noisePlayer) {
		this.sequence = sequence;
		this.noisePlayer = noisePlayer;
		playTasks = new ArrayList<>();
		this.timeStamp = 0;
	}

	public boolean isLooping() {
		return loop;
	}

	public void setLooping(boolean loop) {
		this.loop = loop;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public double getCurrentTime() {
		return timeStamp + GameClock.getTime() - startTime;
	}

	public void setCurrentTime(double currentTime) {
		this.timeStamp = currentTime;
		pause();
		play();
	}

	public void play() {
		if (isPlaying) {
			stop();
		}
		isPlaying = true;
		startTime = GameClock.getTime();
		
		List<NoiseSequenceNode> nodes = sequence.getNodes();
		for (NoiseSequenceNode node : nodes) {
			double time = node.getTime();
			if (time < timeStamp) {
				continue;
			}
			Noise noise = node.getNoise();
			double delay = time - timeStamp;
			DelayedTask playTask = new DelayedTask(delay) {
				@Override
				protected void run() {
					noisePlayer.accept(noise);
				}
			};
			playTasks.add(playTask);
			playTask.schedule();
		}

		double duration = sequence.getDuration();
		DelayedTask finishTask = new DelayedTask(duration) {
			@Override
			public void run() {
				reset();
				if (loop) {
					play();
				}
			}
		};
		playTasks.add(finishTask);
		finishTask.schedule();
	}

	public void pause() {
		timeStamp = getCurrentTime();
		isPlaying = false;
		for (DelayedTask playTask : playTasks) {
			playTask.cancel();
		}
		playTasks.clear();
	}

	public void stop() {
		for (DelayedTask playTask : playTasks) {
			playTask.cancel();
		}
		reset();
	}

	private void reset() {
		isPlaying = false;
		timeStamp = 0;
	}

}
