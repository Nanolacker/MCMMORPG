package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterRemoveEvent;
import com.mcmmorpg.common.time.DelayedTask;

/**
 * A sequence of interactions with pauses that can be sent to a player. Good for
 * dialogue.
 */
public abstract class InteractionSequence {

	private static final HashMap<PlayerCharacter, List<InteractionSequence>> playingSequencesMap = new HashMap<>();

	private final int interactionCount;
	private final double period;
	private final Map<PlayerCharacter, MessageSequencePlayer> sequencePlayerMap = new HashMap<>();

	/**
	 * @param period
	 *            the delay between messages in seconds
	 */
	public InteractionSequence(int interactionCount, double period) {
		this.interactionCount = interactionCount;
		this.period = period;
	}

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onRemovePC(PlayerCharacterRemoveEvent event) {
				PlayerCharacter pc = event.getPlayerCharacter();
				List<InteractionSequence> sequences = playingSequencesMap.get(pc);
				if (sequences != null) {
					for (InteractionSequence sequence : sequences) {
						sequence.cancel(pc);
					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public final void advance(PlayerCharacter pc) {
		if (!sequencePlayerMap.containsKey(pc)) {
			if (!playingSequencesMap.containsKey(pc)) {
				playingSequencesMap.put(pc, new ArrayList<>());
			}
			List<InteractionSequence> sequences = playingSequencesMap.get(pc);
			sequences.add(this);
			MessageSequencePlayer sequencePlayer = new MessageSequencePlayer(this, pc);
			sequencePlayerMap.put(pc, sequencePlayer);
		}
		MessageSequencePlayer sequencePlayer = sequencePlayerMap.get(pc);
		sequencePlayer.advance();
	}

	public final void cancel(PlayerCharacter pc) {
		MessageSequencePlayer sequencePlayer = sequencePlayerMap.get(pc);
		sequencePlayer.cancel();
	}

	protected abstract void onAdvance(PlayerCharacter pc, int interactionIndex);

	private static class MessageSequencePlayer {
		private final InteractionSequence sequence;
		private final PlayerCharacter pc;
		private int interactionIndex;
		private DelayedTask advanceTask;

		private MessageSequencePlayer(InteractionSequence sequence, PlayerCharacter pc) {
			this.sequence = sequence;
			this.pc = pc;
			this.interactionIndex = 0;
		}

		private void advance() {
			sequence.onAdvance(pc, interactionIndex);
			interactionIndex++;
			final int nextInteractionIndex = interactionIndex;
			if (interactionIndex == sequence.interactionCount) {
				playingSequencesMap.get(pc).remove(sequence);
				sequence.sequencePlayerMap.remove(pc);
			} else {
				advanceTask = new DelayedTask(sequence.period) {
					@Override
					protected void run() {
						if (interactionIndex == nextInteractionIndex) {
							// check if the sequence was advanced elsewhere
							advance();
						}
					}
				};
				advanceTask.schedule();
			}
		}

		private void cancel() {
			advanceTask.cancel();
		}

	}

}
