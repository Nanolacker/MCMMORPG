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
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.Bandit;
import com.mcmmorpg.impl.npcs.CultistMage;
import com.mcmmorpg.impl.npcs.CultistSummoner;
import com.mcmmorpg.impl.npcs.GelatinousCube;
import com.mcmmorpg.impl.npcs.SewerRat;

public class FlintonSewersListener implements Listener {

	private static final Noise SEWAGE_DAMAGE_NOISE = new Noise(Sound.BLOCK_SLIME_BLOCK_HIT);
	private static final Location[] BANDIT_LOCATIONS = {};
	private static final Location[] GELATINOUS_CCUBE_LOCATIONS = {};
	private static final Location[] CULTIST_MAGE_LOCATOINS = {};
	private static final Location[] CULTIST_SUMMONER_LOCATIONS = {};
	private static final Location[] RAT_LOCATIONS = {};

	private Source sewage;
	private Collider innerBounds;

	public FlintonSewersListener() {
		setUpBounds();
		setUpSewage();
		spawnNpcs();
	}

	private void setUpBounds() {
		innerBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(ChatColor.GRAY + "Flinton Sewers");
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.DUNGEON);
				}
			}
		};
		innerBounds.setActive(true);
		Collider outerBounds = new Collider(Worlds.ELADRADOR, 0, 0, 0, 0, 0, 0) {
			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(ChatColor.GREEN + "Flinton");
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.VILLAGE);
				}
			}
		};
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
		for (Location location : BANDIT_LOCATIONS) {
			new Bandit(location).setAlive(true);
		}
		for (Location location : GELATINOUS_CCUBE_LOCATIONS) {
			new GelatinousCube(location).setAlive(true);
		}
		for (Location location : CULTIST_MAGE_LOCATOINS) {
			new CultistMage(location).setAlive(true);
		}
		for (Location location : CULTIST_SUMMONER_LOCATIONS) {
			new CultistSummoner(location).setAlive(true);
		}
		for (Location location : RAT_LOCATIONS) {
			new SewerRat(location).setAlive(true);
		}
	}

}
