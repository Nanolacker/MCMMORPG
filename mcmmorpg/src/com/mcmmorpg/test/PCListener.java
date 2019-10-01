package com.mcmmorpg.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.persistence.PlayerCharacterSaveData;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.ActionBar;

public class PCListener implements Listener {

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerClass playerClass = PlayerClass.forName("Fighter");
		World world = Bukkit.getWorld("world");
		Location startingLocation = new Location(world, 141, 70, 66);
		PlayerCharacterSaveData saveData = PlayerCharacterSaveData.createFreshSaveData(player, playerClass,
				startingLocation);
		PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
		pc.grantXP(90);
	}

	@EventHandler
	private void onPunch(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR) {
			DamageCollider collider = new DamageCollider(player.getLocation());
			collider.setActive(true);
			new DelayedTask(0.5) {
				@Override
				protected void run() {
					collider.setActive(false);
				}
			}.schedule();
		}
	}

}
