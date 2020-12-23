package com.mcmmorpg.test;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mcmmorpg.common.ai.CharacterNavigator;
import com.mcmmorpg.common.ai.CharacterPathFollower;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.util.BukkitUtility;

public class AiTestNpc extends NonPlayerCharacter {

	private Villager entity;

	protected AiTestNpc(Location spawnLocation) {
		super("AI Test", 2, spawnLocation);
		CharacterPathFollower pathFollower = new CharacterPathFollower(this);
		pathFollower.setStoppingDistance(3);
		CharacterNavigator navigator = new CharacterNavigator(pathFollower);

		Listener listener = new Listener() {
			@EventHandler
			private void onRightClick(PlayerInteractEvent event) {
				if (!isSpawned()) {
					return;
				}
				Player player = event.getPlayer();
				Location destination = player.getLocation();
				navigator.setDestination(destination);
			}
		};
		EventManager.registerEvents(listener);
	}

	@Override
	protected void spawn() {
		super.spawn();
		entity = (Villager) BukkitUtility.spawnNonpersistentEntity(getLocation(), EntityType.VILLAGER);
		entity.setAI(false);
		entity.setSilent(true);
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
