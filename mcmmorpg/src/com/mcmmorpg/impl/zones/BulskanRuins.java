package com.mcmmorpg.impl.zones;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.Zone;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BulskanUndead;

public class BulskanRuins extends Zone implements Listener {

	public BulskanRuins() {
		super("Bulskan Ruins", Worlds.ELADRADOR, new BoundingBox[0], ChatColor.GRAY);
		EventManager.registerEvents(this);
		createNPCs();
	}

	private void createNPCs() {
		BulskanUndead enemy1 = new BulskanUndead(1, new Location(Worlds.ELADRADOR, 0, 0, 0));
		enemy1.setAlive(true);
	}

	@Override
	public void onEnter(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		player.sendTitle(ChatColor.GRAY + "Bulskan Ruins", "");
		Noise noise = new Noise(Sound.AMBIENT_CAVE);
		noise.play(player);
	}

	@Override
	public void onExit(PlayerCharacter pc) {
	}

}
