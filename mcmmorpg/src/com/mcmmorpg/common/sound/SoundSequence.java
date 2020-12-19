package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of sounds that can be played with a sound sequence player.
 */
public class SoundSequence {

	private List<SoundSequenceNode> nodes;
	private double duration;

	/**
	 * Creates a sound sequence with the specified duration in seconds.
	 */
	public SoundSequence(double duration) {
		nodes = new ArrayList<>();
		this.duration = duration;
	}

	/**
	 * Adds a sound to this sequence at the specified time in seconds.
	 */
	public void add(Noise sound, double time) {
		if (time > duration) {
			throw new IllegalArgumentException("Time is greater than duration");
		}
		SoundSequenceNode node = new SoundSequenceNode(sound, time);
		nodes.add(node);
	}

	/**
	 * Returns this sequence's duration in seconds.
	 */
	public double getDuration() {
		return duration;
	}

	List<SoundSequenceNode> getNodes() {
		return nodes;
	}

	static class SoundSequenceNode {
		private final Noise sound;
		private final double time;

		SoundSequenceNode(Noise sound, double timeSeconds) {
			this.sound = sound;
			this.time = timeSeconds;
		}

		Noise getSound() {
			return sound;
		}

		/**
		 * In seconds.
		 */
		double getTime() {
			return time;
		}
	}

}
