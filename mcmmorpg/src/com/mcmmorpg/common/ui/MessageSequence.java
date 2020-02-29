package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterRemoveEvent;
import com.mcmmorpg.common.time.DelayedTask;

/**
 * A sequence of messages with pauses that can be sent to a player. Good for
 * dialogue.
 */
public class MessageSequence {

	private static final HashMap<PlayerCharacter, List<MessageSequence>> playingSequencesMap = new HashMap<>();

	private final String[] messages;
	private final double period;
	private final Map<PlayerCharacter, MessageSequencePlayer> sequencePlayerMap = new HashMap<>();

	/**
	 * @param period the delay between messages in seconds
	 */
	public MessageSequence(String[] messages, double period) {
		this.messages = messages;
		this.period = period;
	}

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onRemovePC(PlayerCharacterRemoveEvent event) {
				PlayerCharacter pc = event.getPlayerCharacter();
				List<MessageSequence> sequences = playingSequencesMap.get(pc);
				if (sequences != null) {
					for (MessageSequence sequence : sequences) {
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
			List<MessageSequence> sequences = playingSequencesMap.get(pc);
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

	/**
	 * Override in subclasses to provide additional functionality.
	 */
	protected void onAdvance(PlayerCharacter pc, int messageIndex) {
	}

	private static class MessageSequencePlayer {
		private final MessageSequence sequence;
		private final PlayerCharacter pc;
		private int messageIndex;
		private DelayedTask advanceTask;

		private MessageSequencePlayer(MessageSequence sequence, PlayerCharacter pc) {
			this.sequence = sequence;
			this.pc = pc;
			messageIndex = 0;
		}

		private void advance() {
			String message = sequence.messages[messageIndex];
			pc.sendMessage(message);
			sequence.onAdvance(pc, messageIndex);
			messageIndex++;
			final int nextMessageIndex = messageIndex;
			if (messageIndex == sequence.messages.length) {
				playingSequencesMap.get(pc).remove(sequence);
				sequence.sequencePlayerMap.remove(pc);
			} else {
				advanceTask = new DelayedTask(sequence.period) {
					@Override
					protected void run() {
						if (messageIndex == nextMessageIndex) {
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
