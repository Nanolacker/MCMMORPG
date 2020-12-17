package com.mcmmorpg.common.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.audio.AudioSequence.AudioSequenceNode;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.Clock;

/**
 * Plays an audio sequence ambiently or to a specific player.
 */
public class AudioSequencePlayer {

	private final AudioSequence audioSequence;
	private final Consumer<AudioSource> audioPlayer;
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
	 * Create an audio sequence player to play strictly to the specific player.
	 */
	public AudioSequencePlayer(AudioSequence sequence, Player player) {
		this(sequence, audioSource -> audioSource.play(player));
	}

	/**
	 * Create an audio sequence player to play strictly to the specific player
	 * character.
	 */
	public AudioSequencePlayer(AudioSequence sequence, PlayerCharacter pc) {
		this(sequence, pc.getPlayer());
	}

	/**
	 * Create an audio sequence player to play strictly to the specific player from
	 * the specified source.
	 */
	public AudioSequencePlayer(AudioSequence sequence, Player player, Location source) {
		this(sequence, audioSource -> audioSource.play(player, source));
	}

	/**
	 * Create an audio sequence player to play strictly to the specific player
	 * character from the specified source.
	 */
	public AudioSequencePlayer(AudioSequence audioSequence, PlayerCharacter pc, Location source) {
		this(audioSequence, pc.getPlayer(), source);
	}

	/**
	 * Create an audio sequence player to play from the specified source.
	 */
	public AudioSequencePlayer(AudioSequence audioSequence, Location source) {
		this(audioSequence, audioSource -> audioSource.play(source));
	}

	private AudioSequencePlayer(AudioSequence audioSequence, Consumer<AudioSource> audioPlayer) {
		this.audioSequence = audioSequence;
		this.audioPlayer = audioPlayer;
		playTasks = new ArrayList<>();
		this.timeStamp = 0;
	}

	/**
	 * Returns the audio sequence played.
	 */
	public AudioSequence getAudioSequence() {
		return audioSequence;
	}

	/**
	 * Returns whether or not this audio sequence player will loop (i.e. repeat).
	 */
	public boolean isLooping() {
		return loop;
	}

	/**
	 * Sets whether or not this audio sequence player will loop (i.e. repeat).
	 */
	public void setLooping(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Returns whether or not this audio sequence player is playing.
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
	 * Play the audio sequence.
	 */
	public void play() {
		if (isPlaying) {
			stop();
		}
		isPlaying = true;
		startTime = Clock.getTime();

		List<AudioSequenceNode> nodes = audioSequence.getNodes();
		for (AudioSequenceNode node : nodes) {
			double time = node.getTime();
			if (time < timeStamp) {
				continue;
			}
			AudioSource audioSource = node.getAudioSource();
			double delay = time - timeStamp;
			DelayedTask playTask = new DelayedTask(delay) {
				@Override
				protected void run() {
					audioPlayer.accept(audioSource);
				}
			};
			playTasks.add(playTask);
			playTask.schedule();
		}

		double duration = audioSequence.getDuration();
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
	 * Pause this audio sequence player, maintaining the time in the sequence.
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
	 * Stop this audio sequence player. The time of this audio sequence player is
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
