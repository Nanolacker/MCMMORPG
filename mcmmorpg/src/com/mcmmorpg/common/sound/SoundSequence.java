package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

public class SoundSequence {

	private List<SoundSequenceNode> nodes;
	/**
	 * In seconds.
	 */
	private double duration;

	/**
	 * 
	 * @param duration
	 *            in seconds
	 */
	public SoundSequence(double duration) {
		nodes = new ArrayList<>();
		this.duration = duration;
	}

	/**
	 * @param time
	 *            the second at which the noise should play in the sequence
	 */
	public void add(Noise sound, double time) {
		if (time > duration) {
			throw new IllegalArgumentException("Time is greater than duration");
		}
		SoundSequenceNode node = new SoundSequenceNode(sound, time);
		nodes.add(node);
	}

	/**
	 * In seconds.
	 * 
	 * @return
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
