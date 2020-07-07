package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;

/**
 * Class that contains static methods pertaining to tutorials.
 */
public class Tutorial {

	private Tutorial() {
	}

	public static void message(PlayerCharacter pc, String message) {
		message(pc.getPlayer(), message);
	}

	public static void message(Player player, String message) {
		message = ChatColor.GRAY + "[" + ChatColor.GREEN + "Tutorial" + ChatColor.GRAY + "]: " + ChatColor.WHITE
				+ message;
		player.sendMessage(message);
	}

	public static void message(PlayerCharacter pc, String message, double delay) {
		message(pc.getPlayer(), message, delay);
	}

	public static void message(Player player, String message, double delay) {
		new DelayedTask(delay) {
			@Override
			protected void run() {
				message(player, message);
			}
		}.schedule();
	}

}
