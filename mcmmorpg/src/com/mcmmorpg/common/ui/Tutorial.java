package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;

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

}
