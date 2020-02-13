package com.mcmmorpg.common.ui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;

/**
 * A sequence of messages with pauses that can be sent to a player. Can be used
 * for dialogue.
 */
public class MessageSequence {

	private static final Set<Player> players = new HashSet<>();

	private final String[] messages;
	private final double period;

	/**
	 * @param period the delay between messages in seconds
	 */
	public MessageSequence(String[] messages, double period) {
		this.messages = messages;
		this.period = period;
	}

	public void play(PlayerCharacter pc) {
		play(pc.getPlayer());
	}

	public void play(Player player) {
		players.add(player);
		play0(player, 0);
	}

	private void play0(Player player, int messageIndex) {
		if (messageIndex == messages.length || !Bukkit.getOnlinePlayers().contains(player)) {
			return;
		}
		String message = messages[messageIndex];
		player.sendMessage(message);
		DelayedTask nextMessage = new DelayedTask(period) {
			@Override
			protected void run() {
				play0(player, messageIndex + 1);
			}
		};
		nextMessage.schedule();
	}

}
