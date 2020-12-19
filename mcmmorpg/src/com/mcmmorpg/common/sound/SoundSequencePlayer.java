package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.SoundSequence.Node;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.Clock;

/**
 * Plays a sound sequence ambiently or to a specific player.
 */
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

	/**
	 * Create a sound sequence player to play strictly to the specific player.
	 */
	public SoundSequencePlayer(SoundSequence sequence, Player player) {
		this(sequence, noise -> noise.play(player));
	}

	/**
	 * Create a sound sequence player to play strictly to the specific player
	 * character.
	 */
	public SoundSequencePlayer(SoundSequence sequence, PlayerCharacter pc) {
		this(sequence, pc.getPlayer());
	}

	/**
	 * Create a sound sequence player to play strictly to the specific player from
	 * the specified source.
	 */
	public SoundSequencePlayer(SoundSequence sequence, Player player, Location source) {
		this(sequence, noise -> noise.play(player, source));
	}

	/**
	 * Create a sound sequence player to play strictly to the specific player
	 * character from the specified source.
	 */
	public SoundSequencePlayer(SoundSequence sequence, PlayerCharacter pc, Location source) {
		this(sequence, pc.getPlayer(), source);
	}

	/**
	 * Create a sound sequence player to play from the specified source.
	 */
	public SoundSequencePlayer(SoundSequence sequence, Location source) {
		this(sequence, noise -> noise.play(source));
	}

	private SoundSequencePlayer(SoundSequence sequence, Consumer<Noise> noisePlayer) {
		this.sequence = sequence;
		this.noisePlayer = noisePlayer;
		playTasks = new ArrayList<>();
		this.timeStamp = 0;
	}

	/**
	 * Returns the sound sequence played.
	 */
	public SoundSequence getSequence() {
		return sequence;
	}

	/**
	 * Returns whether or not this sound sequence player will loop (i.e. repeat).
	 */
	public boolean isLooping() {
		return loop;
	}

	/**
	 * Sets whether or not this sound sequence player will loop (i.e. repeat).
	 */
	public void setLooping(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Returns whether or not this sound sequence player is playing.
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * Returns how many seconds into the sequence has played.
	 */
	public double getCurrentTime() {
		return timeStamp + Clock.getTime() - startTime;
	}

	/**
	 * Sets how many seconds into the sequence has played. Use this to rewind or
	 * fast forward.
	 */
	public void setCurrentTime(double currentTime) {
		this.timeStamp = currentTime;
		pause();
		play();
	}

	/**
	 * Play the sound sequence.
	 */
	public void play() {
		if (isPlaying) {
			stop();
		}
		isPlaying = true;
		startTime = Clock.getTime();

		List<Node> nodes = sequence.getNodes();
		for (Node node : nodes) {
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

	/**
	 * Pause this sound sequence player, maintaining the time in the sequence.
	 */
	public void pause() {
		timeStamp = getCurrentTime();
		isPlaying = false;
		for (DelayedTask playTask : playTasks) {
			playTask.cancel();
		}
		playTasks.clear();
	}

	/**
	 * Stop this sound sequence player. The time of this sound sequence player is
	 * reset to the beginning of the sequence.
	 */
	public void stop() {
		for (DelayedTask playTask : playTasks) {
			if (playTask.isScheduled()) {
				playTask.cancel();
			}
		}
		reset();
	}

	private void reset() {
		isPlaying = false;
		timeStamp = 0;
	}

}
