package com.mcmmorpg.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {

	@EventHandler
	private void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	private void onCombust(EntityCombustEvent event) {
		event.setCancelled(true);
	}

}
