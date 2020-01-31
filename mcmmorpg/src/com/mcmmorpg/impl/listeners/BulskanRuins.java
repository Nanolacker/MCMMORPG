package com.mcmmorpg.impl.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Lever;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.TitleMessage;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BulskanUndead;

public class BulskanRuins implements Listener {

	public BulskanRuins() {
		setUpBounds();
		spawnNpcs();
	}

	private void setUpBounds() {
		TitleMessage entranceMessage = new TitleMessage(ChatColor.DARK_GRAY + "Bulskan Ruins",
				ChatColor.GOLD + "Level 5");
		TitleMessage exitMessage = new TitleMessage(ChatColor.GREEN + "Melcher Plains", ChatColor.GOLD + "Level 1");
		Noise entranceNoise = new Noise(Sound.AMBIENT_CAVE);
		Collider entranceCollider = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			public void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					Player player = pc.getPlayer();
					entranceMessage.sendTo(player);
					entranceNoise.play(player);
				}
			}
		};

		Collider exitCollider = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			public void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					Player player = pc.getPlayer();
					exitMessage.sendTo(player);
				}
			}
		};

		entranceCollider.setActive(true);
		exitCollider.setActive(true);
	}

	private void spawnNpcs() {
		Location[] undeadLocations = { new Location(Worlds.ELADRADOR, 0, 0, 0) };
		for (Location location : undeadLocations) {
			BulskanUndead undead = new BulskanUndead(4, location, 30);
			undead.setAlive(true);
		}
	}

	private void onButtonPress(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Block clicked = event.getClickedBlock();
		Location location = clicked.getLocation();
		if (location == null) {
			Lever lever = (Lever) clicked;
			lever.setPowered(true);
		}

	}

}
