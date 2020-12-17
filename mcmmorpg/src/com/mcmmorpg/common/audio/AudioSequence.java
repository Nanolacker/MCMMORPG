package com.mcmmorpg.common.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of sounds that can be played with a sound sequence player.
 */
public class AudioSequence {

	private List<AudioSequenceNode> nodes;
	private double duration;

	/**
	 * Creates a sound sequence with the specified duration in seconds.
	 */
	public AudioSequence(double duration) {
		nodes = new ArrayList<>();
		this.duration = duration;
	}

	/**
	 * Adds a sound to this sequence at the specified time in seconds.
	 */
	public void add(AudioSource sound, double time) {
		if (time > duration) {
			throw new IllegalArgumentException("Time is greater than duration");
		}
		AudioSequenceNode node = new AudioSequenceNode(sound, time);
		nodes.add(node);
	}

	/**
	 * Returns this sequence's duration in seconds.
	 */
	public double getDuration() {
		return duration;
	}

	List<AudioSequenceNode> getNodes() {
		return nodes;
	}

	static class AudioSequenceNode {
		private final AudioSource audioSource;
		private final double time;

		AudioSequenceNode(AudioSource audioSource, double timeSeconds) {
			this.audioSource = audioSource;
			this.time = timeSeconds;
		}

		AudioSource getAudioSource() {
			return audioSource;
		}

		/**
		 * In seconds.
		 */
		double getTime() {
			return time;
		}
	}

}
