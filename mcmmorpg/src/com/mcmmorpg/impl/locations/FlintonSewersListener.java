package com.mcmmorpg.impl.locations;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Bandit;
import com.mcmmorpg.impl.npcs.ColossalGelatinousCube;
import com.mcmmorpg.impl.npcs.CultistMage;
import com.mcmmorpg.impl.npcs.CultistSummoner;
import com.mcmmorpg.impl.npcs.FlintonSewersAlchemist;
import com.mcmmorpg.impl.npcs.FlintonSewersRat;
import com.mcmmorpg.impl.npcs.FlintonSewersXylphanos;
import com.mcmmorpg.impl.npcs.GelatinousCube;
import com.mcmmorpg.impl.npcs.SmallGelatinousCube;

public class FlintonSewersListener implements Listener {

	private static final Noise SLUDGE_DAMAGE_NOISE = new Noise(Sound.BLOCK_SLIME_BLOCK_HIT);
	private static final int MAX_SMALL_GELATINOUS_CUBE_COUNT_PER_PLAYER_CHARACTER = 5;
	private static final Location ALCHEMIST_LOCATION = new Location(Worlds.ELADRADOR, -286, 82, 135);
	private static final Location[] BANDIT_LOCATIONS = {};
	private static final Location[] GELATINOUS_CCUBE_LOCATIONS = {};
	private static final Location[] COLOSSAL_GELATINOUS_CUBE_LOCATIONS = {};
	private static final Location[] CULTIST_MAGE_LOCATOINS = {};
	private static final Location[] CULTIST_SUMMONER_LOCATIONS = {};
	private static final Location[] RAT_LOCATIONS = {};
	private static final Location[] LOOT_CHEST_LOCATIONS = {};

	public static final Map<PlayerCharacter, Integer> smallGelatinousCubeCounts = new HashMap<>();

	private Source sludge;
	private Collider innerBounds;

	public FlintonSewersListener() {
		setBounds();
		setUpSludge();
		spawnNpcs();
		setUpPortcullises();
		placeLootChests();
	}

	private void setBounds() {
		innerBounds = new Collider(Worlds.ELADRADOR, -999, -999, -999, 999, 999, 999) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.FLINTON_SEWERS);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.DUNGEON);
					smallGelatinousCubeCounts.put(pc, 0);
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
					smallGelatinousCubeCounts.remove(pc);
				}
			}
		};
		outerBounds.setActive(true);
	}

	private void setUpSludge() {
		sludge = new Source() {
			@Override
			public String getName() {
				return ChatColor.RED + "Sludge";
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
							pc.damage(2, sludge);
							SLUDGE_DAMAGE_NOISE.play(floorLocation);
							if (smallGelatinousCubeCounts
									.get(pc) < MAX_SMALL_GELATINOUS_CUBE_COUNT_PER_PLAYER_CHARACTER) {
								new SmallGelatinousCube(pc.getLocation(), pc).setAlive(true);
							}
						}
					}
				}
			}
		}.schedule();
	}

	private void spawnNpcs() {
		new FlintonSewersAlchemist(ALCHEMIST_LOCATION).setAlive(true);
		for (Location location : BANDIT_LOCATIONS) {
			new Bandit(location).setAlive(true);
		}
		for (Location location : GELATINOUS_CCUBE_LOCATIONS) {
			new GelatinousCube(location).setAlive(true);
		}
		for (Location location : COLOSSAL_GELATINOUS_CUBE_LOCATIONS) {
			new ColossalGelatinousCube(location).setAlive(true);
		}
		for (Location location : CULTIST_MAGE_LOCATOINS) {
			new CultistMage(location).setAlive(true);
		}
		for (Location location : CULTIST_SUMMONER_LOCATIONS) {
			new CultistSummoner(location).setAlive(true);
		}
		for (Location location : RAT_LOCATIONS) {
			new FlintonSewersRat(location).setAlive(true);
		}
		new FlintonSewersXylphanos(
				new Location(Worlds.ELADRADOR, -1016.773831, 70.000000, 190.152897, 62.335854f, 31.613012f))
						.setAlive(true);
	}

	private void setUpPortcullises() {
		new FlintonSewersPortcullis(new Location(Worlds.ELADRADOR, -1006.5, 70, 212.5), false, Items.APPRENTICE_STAFF);
	}

	private void placeLootChests() {
		
	}

}
