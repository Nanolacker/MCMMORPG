package com.mcmmorpg.common.character;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CharacterListener implements Listener {

	private static final double PLAYER_HUNGER_UPDATE_PERIOD = 0.5;

	public CharacterListener() {
	}

	@EventHandler
	private void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

}
