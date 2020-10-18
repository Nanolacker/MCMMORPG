package com.mcmmorpg.test;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mcmmorpg.common.ai.CharacterNavigator;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.util.BukkitUtility;

public class AiTestNpc extends NonPlayerCharacter {

	private static final double SPEED = 2;

	private Villager entity;

	protected AiTestNpc(Location spawnLocation) {
		super("AI Test", 2, spawnLocation);
		CharacterNavigator navigator = new CharacterNavigator(this);
		navigator.setSpeed(SPEED);
		navigator.setEnabled(true);

		Listener listener = new Listener() {
			@EventHandler
			private void onRightClick(PlayerInteractEvent event) {
				Player player = event.getPlayer();
				navigator.setDestination(player.getLocation());
			}
		};
		EventManager.registerEvents(listener);
	}

	@Override
	protected void spawn() {
		super.spawn();
		entity = (Villager) BukkitUtility.spawnNonpersistentEntity(getLocation(), EntityType.VILLAGER);
		entity.setAI(false);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		if (isSpawned()) {
			entity.teleport(location);
		}
	}

}
