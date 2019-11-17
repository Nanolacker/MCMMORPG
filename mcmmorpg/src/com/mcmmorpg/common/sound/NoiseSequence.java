package com.mcmmorpg.common.sound;

import java.util.ArrayList;
import java.util.List;

public class NoiseSequence {

	private List<NoiseSequenceNode> nodes;

	public NoiseSequence() {
		nodes = new ArrayList<>();
	}

	public void add(Noise noise, double timeSeconds) {
		NoiseSequenceNode node = new NoiseSequenceNode(noise, timeSeconds);
		nodes.add(node);
	}

	List<NoiseSequenceNode> getNodes() {
		return nodes;
	}

	static class NoiseSequenceNode {

		private final Noise noise;
		private final double timeSeconds;

		NoiseSequenceNode(Noise noise, double timeSeconds) {
			this.noise = noise;
			this.timeSeconds = timeSeconds;
		}

		Noise getNoise() {
			return noise;
		}

		double getTimeSeconds() {
			return timeSeconds;
		}

	}

}
