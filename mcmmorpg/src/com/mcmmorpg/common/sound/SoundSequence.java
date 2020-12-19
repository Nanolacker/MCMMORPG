package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of sounds that can be played with a sound sequence player.
 */
public class SoundSequence {

	private final double duration;
	private final List<Node> sounds;

	/**
	 * Creates a sound sequence with the specified duration in seconds.
	 */
	public SoundSequence(double duration) {
		this.duration = duration;
		sounds = new ArrayList<>();
	}

	/**
	 * Returns this sequence's duration in seconds.
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Adds a sound to this sequence at the specified time in seconds.
	 */
	public void add(Noise sound, double time) {
		if (time > duration) {
			throw new IllegalArgumentException("Time is greater than duration");
		}
		Node node = new Node(sound, time);
		sounds.add(node);
	}

	List<Node> getNodes() {
		return sounds;
	}

	static class Node {
		private final double time;
		private final Noise sound;

		Node(Noise sound, double timeSeconds) {
			this.sound = sound;
			this.time = timeSeconds;
		}

		/**
		 * In seconds.
		 */
		double getTime() {
			return time;
		}

		Noise getSound() {
			return sound;
		}
	}

}
