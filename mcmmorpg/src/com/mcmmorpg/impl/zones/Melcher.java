package com.mcmmorpg.impl.zones;

import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.Zone;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.impl.Worlds;

import net.md_5.bungee.api.ChatColor;

public class Melcher extends Zone {

	public Melcher() {
		super("Melcher", Worlds.ELADRADOR, new BoundingBox[0]);
	}

	@Override
	public void onEnter(PlayerCharacter pc) {
		pc.getPlayer().sendTitle(ChatColor.GREEN + "Melcher", "");
	}

	@Override
	public void onExit(PlayerCharacter pc) {
		pc.getPlayer().sendTitle("The Wilds", "");
	}

}
