package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Bandit;
import com.mcmmorpg.impl.npcs.BanditLeader;

public class FlintonListener implements Listener {

	public FlintonListener() {
		setUpBounds();
		spawnNpcs();
	}

	private void setUpBounds() {
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
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.WILDNERNESS);
				}
			}
		};
		exitBounds.setActive(true);
	}

	private void spawnNpcs() {
		Location[] banditLocations = {};
		for (Location location : banditLocations) {
			new Bandit(location).setAlive(true);
		}
		Location bossLocation = new Location(Worlds.ELADRADOR, 0, 0, 0);
		new BanditLeader(bossLocation).setAlive(true);
	}

}
