package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

public class NoiseSequence {

	private List<NoiseSequenceNode> nodes;
	/**
	 * In seconds.
	 */
	private double duration;

	/**
	 * 
	 * @param duration in seconds
	 */
	public NoiseSequence(double duration) {
		nodes = new ArrayList<>();
		this.duration = duration;
	}

	/**
	 * 
	 * @param noise
	 * @param time  the second at which the noise should play in the sequence
	 */
	public void add(Noise noise, double time) {
		if (time > duration) {
			throw new IllegalArgumentException("Time is greater than duration");
		}
		NoiseSequenceNode node = new NoiseSequenceNode(noise, time);
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

	List<NoiseSequenceNode> getNodes() {
		return nodes;
	}

	static class NoiseSequenceNode {

		private final Noise noise;
		private final double time;

		NoiseSequenceNode(Noise noise, double timeSeconds) {
			this.noise = noise;
			this.time = timeSeconds;
		}

		Noise getNoise() {
			return noise;
		}

		/**
		 * In seconds.
		 */
		double getTime() {
			return time;
		}

	}

}
