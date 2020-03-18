package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.SoundtrackManager;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.TrainingDummy;
import com.mcmmorpg.impl.npcs.WildBoar;
import com.mcmmorpg.impl.npcs.Witch;

public class BulskanRuinsListener implements Listener {

	private static String ZONE_NAME = ChatColor.GRAY + "Bulskan Ruins";

	public BulskanRuinsListener() {
		setUpBounds();
		spawnNpcs();
	}

	private void setUpBounds() {
		Collider entranceCollider = new Collider(Worlds.ELADRADOR, 135, 65, -57, 207, 85, -17) {
			@Override
			public void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					if (pc.getZone().equals(ZONE_NAME)) {
						return;
					}
					pc.setZone(ZONE_NAME);
					pc.getSoundTrackPlayer().setSoundtrack(SoundtrackManager.DUNGEON);
				}
			}
		};

		Collider exitCollider = new Collider(Worlds.ELADRADOR, 129, 0, -63, 210, 85, -13) {
			@Override
			public void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					if (!pc.getZone().equals(ZONE_NAME)) {
						return;
					}
					pc.setZone(ChatColor.GREEN + "Melcher");
					pc.getSoundTrackPlayer().setSoundtrack(null);
				}
			}
		};

		entranceCollider.setActive(true);
		exitCollider.setActive(true);
	}

	private void spawnNpcs() {
		Location[] locations = { new Location(Worlds.ELADRADOR, 174, 67, -35),
				new Location(Worlds.ELADRADOR, 170, 67, -33), new Location(Worlds.ELADRADOR, 155, 67, -36),
				new Location(Worlds.ELADRADOR, 145, 67, -49), new Location(Worlds.ELADRADOR, 190, 67, -42) };

		// new Highwayman(5, locations[0], 10).setAlive(true);
		// new GelatinousCube(5, new Location(Worlds.ELADRADOR, 170, 70, 10),
		// true).setAlive(true);
		// for (int i = 0; i < 5; i++) {
		// new Bat(3, new Location(Worlds.ELADRADOR, 170, 70, 0), 20).setAlive(true);
		// }
		// new RottenDweller(locations[1]).setAlive(true);
		new TrainingDummy(locations[0]).setAlive(true);
		new WildBoar(1, locations[2]).setAlive(true);
		Witch witch = new Witch(ChatColor.GREEN + "Witch", 7, locations[3]) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				say("I am a witch!", pc);
			}
		};
		witch.setAlive(true);
	}

	@EventHandler
	private void onButtonPress(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		// Block clicked = event.getClickedBlock();
		// Location location = clicked.getLocation();
		// if (location == null) {
		// Lever lever = (Lever) clicked;
		// lever.setPowered(true);
		// }
	}

}
