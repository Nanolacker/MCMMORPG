package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.sound.SoundSequence.SoundSequenceNode;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.GameClock;

public class SoundSequencePlayer {

	private final SoundSequence sequence;
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

	public SoundSequencePlayer(SoundSequence sequence, Player player) {
		this(sequence, noise -> noise.play(player));
	}

	public SoundSequencePlayer(SoundSequence sequence, Player player, Location source) {
		this(sequence, noise -> noise.play(player, source));
	}

	public SoundSequencePlayer(SoundSequence sequence, Location source) {
		this(sequence, noise -> noise.play(source));
	}

	private SoundSequencePlayer(SoundSequence sequence, Consumer<Noise> noisePlayer) {
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

		List<SoundSequenceNode> nodes = sequence.getNodes();
		for (SoundSequenceNode node : nodes) {
			double time = node.getTime();
			if (time < timeStamp) {
				continue;
			}
			Noise noise = node.getSound();
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
