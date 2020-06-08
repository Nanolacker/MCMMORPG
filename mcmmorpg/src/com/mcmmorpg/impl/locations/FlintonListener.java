package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.constants.RespawnLocations;
import com.mcmmorpg.impl.constants.Soundtracks;
import com.mcmmorpg.impl.constants.Worlds;
import com.mcmmorpg.impl.constants.Zones;
import com.mcmmorpg.impl.npcs.FlintonMayor;
import com.mcmmorpg.impl.npcs.FlintonVillager;

public class FlintonListener implements Listener {

	private static final Location MAYOR_LOCATION = new Location(Worlds.ELADRADOR, -288.242576, 82.000000, 111.697212,
			-88.275375f, 5.199878f);
	private static final Location[] VILLAGER_LOCATIONS = {};

	public FlintonListener() {
		setBounds();
		spawnNpcs();
	}

	private void setBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.FLINTON);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.VILLAGE);
				}
			}
		};
		entranceBounds.setActive(true);
		Collider exitBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.ELADRADOR);
					pc.setRespawnLocation(RespawnLocations.FLINTON);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.WILDNERNESS);
				}
			}
		};
		exitBounds.setActive(true);
	}

	private void spawnNpcs() {
		new FlintonMayor(MAYOR_LOCATION).setAlive(true);
		for (int i = 0; i < VILLAGER_LOCATIONS.length; i++) {
			Location location = VILLAGER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new FlintonVillager(location, male).setAlive(true);
		}
	}

}
