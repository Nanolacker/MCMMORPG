package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.impl.Worlds;

public class FlintonSewersListener implements Listener {

	private static final Noise SEWAGE_DAMAGE_NOISE = new Noise(Sound.BLOCK_SLIME_BLOCK_HIT);

	private Source sewage;
	private Collider innerBounds;

	public FlintonSewersListener() {
		setUpBounds();
		setUpSewage();
		spawnNpcs();
	}

	private void setUpBounds() {
		innerBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0);
		innerBounds.setActive(true);
		Collider outerBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0);
		outerBounds.setActive(true);
	}

	private void setUpSewage() {
		sewage = new Source() {
			@Override
			public String getName() {
				return ChatColor.RED + "Sewage";
			}
		};
		new RepeatingTask(1) {
			@Override
			protected void run() {
				Collider[] colliders = innerBounds.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof PlayerCharacterCollider) {
						PlayerCharacter pc = ((PlayerCharacterCollider) collider).getCharacter();
						Location location = pc.getLocation();
						World world = location.getWorld();
						Location floorLocation = location.subtract(0, 1, 0);
						Block floor = world.getBlockAt(floorLocation);
						if (floor.getType() == Material.GLASS) {
							pc.damage(2, sewage);
							SEWAGE_DAMAGE_NOISE.play(floorLocation);
						}
					}
				}
			}
		}.schedule();
	}

	private void spawnNpcs() {
		Location[] banditLocations = {};
		Location[] gelatinouseCubeLocations = {};
		Location[] cultistMageLocations = {};
		Location[] cultistSummonerLocations = {};
		Location[] ratLocations = {};

		for (Location location : banditLocations) {

		}
		for (Location location : gelatinouseCubeLocations) {

		}
		for (Location location : cultistMageLocations) {

		}
		for (Location location : cultistSummonerLocations) {

		}
		for (Location location : ratLocations) {

		}
	}

}