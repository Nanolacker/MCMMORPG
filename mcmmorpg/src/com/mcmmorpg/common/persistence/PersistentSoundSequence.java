package com.mcmmorpg.common.persistence;

import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.SoundSequence;

/**
 * A representation of a sound sequence that can be serialized.
 */
public class PersistentSoundSequence {
    private String duration;
    private Node[] nodes;

    public SoundSequence toSoundSequence() {
        SoundSequence sequence = new SoundSequence(Double.parseDouble(duration));
        for (Node node : nodes) {
            sequence.add(node.getNoise(), node.getTime());
        }
        return sequence;
    }

    private static class Node {
        private String time;
        private Noise noise;

        private Noise getNoise() {
            return noise;
        }

        private double getTime() {
            return Double.parseDouble(time);
        }
    }
}
