package com.mcmmorpg.impl.locations;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.ui.TitleText;
import com.mcmmorpg.common.utils.CardinalDirection;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Bandit;
import com.mcmmorpg.impl.npcs.ColossalGelatinousCube;
import com.mcmmorpg.impl.npcs.CultistMage;
import com.mcmmorpg.impl.npcs.CultistSummoner;
import com.mcmmorpg.impl.npcs.FlintonSewersAlchemist;
import com.mcmmorpg.impl.npcs.FlintonSewersCultSacrifice;
import com.mcmmorpg.impl.npcs.FlintonSewersRat;
import com.mcmmorpg.impl.npcs.FlintonSewersXylphanos;
import com.mcmmorpg.impl.npcs.GelatinousCube;
import com.mcmmorpg.impl.npcs.SmallGelatinousCube;

public class FlintonSewersListener implements Listener {

	private static final double SLUDGE_DAMAGE = 25;
	private static final Noise SLUDGE_DAMAGE_NOISE = new Noise(Sound.ITEM_BUCKET_FILL);
	private static final int MAX_SMALL_GELATINOUS_CUBE_COUNT_PER_PLAYER_CHARACTER = 5;
	private static final PotionEffect SLUDGE_SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW,
			MathUtils.secondsToTicks(2), 3);
	private static final TitleText ENTER_TEXT = new TitleText(ChatColor.GRAY + "Flinton Sewers", null);
	private static final Noise ENTER_NOISE = new Noise(Sound.ENTITY_WITHER_SPAWN);
	private static final Location ENTRANCE_LOCATION_1 = new Location(Worlds.ELADRADOR, 0, 0, 0);
	private static final Location ENTRANCE_LOCATION_2 = new Location(Worlds.ELADRADOR, 0, 0, 0);
	private static final Location PLAYER_CHARACTER_RESPAWN_LOCATION = new Location(Worlds.ELADRADOR, -269, 42.5, 78,
			180, 0);
	private static final Location ALCHEMIST_LOCATION = new Location(Worlds.ELADRADOR, -274, 42.5, 79, 180, 0);
	private static final Location[] RAT_LOCATIONS = {};
	private static final Location[] GELATINOUS_CCUBE_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -228.948750, 42.000000, 88.313323, -312.155457f, 24.314680f),
			new Location(Worlds.ELADRADOR, -198.906631, 42.000000, 88.703158, -161.027771f, 16.321215f),
			new Location(Worlds.ELADRADOR, -193.926124, 42.000000, 62.000526, -220.109314f, 23.272051f),
			new Location(Worlds.ELADRADOR, -222.661966, 42.000000, 59.364282, -256.602356f, 19.449100f),
			new Location(Worlds.ELADRADOR, -248.124004, 42.000000, 30.040520, -295.872375f, 14.931069f),
			new Location(Worlds.ELADRADOR, -181.924329, 42.000000, 44.055099, -101.996155f, 23.272064f),
			new Location(Worlds.ELADRADOR, -135.286728, 42.000000, 42.417054, -176.021729f, 14.931060f),
			new Location(Worlds.ELADRADOR, -124.372805, 42.000000, 29.453320, -128.408752f, 26.399925f),
			new Location(Worlds.ELADRADOR, -121.771183, 42.000000, -0.369623, -288.276855f, 21.881886f),
			new Location(Worlds.ELADRADOR, -145.307067, 42.000000, -0.818950, -288.276855f, 21.881886f),
			new Location(Worlds.ELADRADOR, -180.115110, 42.000000, -1.162628, -288.277161f, 21.881897f) };
	private static final Location[] COLOSSAL_GELATINOUS_CUBE_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -111.284247, 42.000000, 90.107313, -1.620275f, 81.672386f) };
	private static final Location[] BANDIT_LOCATIONS = {};
	private static final Location[] CULTIST_MAGE_LOCATOINS = {
			new Location(Worlds.ELADRADOR, -408.327771, 42.000000, 56.875984, 154.889282f, 25.704796f),
			new Location(Worlds.ELADRADOR, -407.245511, 42.000000, 49.336538, 155.236816f, 25.704796f) };
	private static final Location[] CULTIST_SUMMONER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -413.452912, 42.000000, 52.049572, 91.239502f, 39.953979f),
			new Location(Worlds.ELADRADOR, -423.961213, 42.000000, 62.317555, 178.472473f, 77.836029f),
			new Location(Worlds.ELADRADOR, -434.167149, 42.000000, 52.071448, 269.180481f, 60.111427f),
			new Location(Worlds.ELADRADOR, -424.141625, 42.000000, 41.386090, 4.057861f, 47.252380f) };
	private static final Location[] SACRIFICE_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -428.389763, 46.000000, 54.862500, 164.465683f, 421.052555f),
			new Location(Worlds.ELADRADOR, -426.321504, 47.000000, 49.862500, 183.232819f, 84.800171f),
			new Location(Worlds.ELADRADOR, -422.642433, 49.000000, 52.862500, 176.977356f, 74.373940f) };
	private static final Location XYLPHANOS_LOCATION = new Location(Worlds.ELADRADOR, -424.004659, 42.000000, 51.943707,
			259.449280f, 79.573784f);
	private static final Location[] LOOT_CHEST_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -204.412130, 44.000000, 70.546381, -99.654922f, 58.026360f),
			new Location(Worlds.ELADRADOR, -207.574530, 44.000000, 49.483283, -1.006195f, 43.777161f),
			new Location(Worlds.ELADRADOR, -219.392660, 44.000000, -2.571479, -95.189117f, 53.160770f),
			new Location(Worlds.ELADRADOR, -159, 44, 14, -84.363235f, 44.819820f),
			new Location(Worlds.ELADRADOR, -188.425285, 44.000000, 104.443225, 251.362183f, 62.544495f),
			new Location(Worlds.ELADRADOR, -354.506557, 44.000000, 57.414971, 259.406860f, 61.154373f),
			new Location(Worlds.ELADRADOR, -354.520152, 44.000000, 29.389884, 263.229248f, 45.862587f),
			new Location(Worlds.ELADRADOR, -357.437898, 44.000000, 4.411252, 0.941406f, 41.344559f),
			new Location(Worlds.ELADRADOR, -273.401182, 44.000000, 9.612517, 166.771606f, 26.400301f) };

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
		innerBounds = new Collider(Worlds.ELADRADOR, -470, 30, -20, -70, 50, 280) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.FLINTON_SEWERS);
					pc.setRespawnLocation(PLAYER_CHARACTER_RESPAWN_LOCATION);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.DUNGEON);
					ENTER_TEXT.apply(pc);
					ENTER_NOISE.play(pc);
					smallGelatinousCubeCounts.put(pc, 0);
				}
			}
		};
		innerBounds.setActive(true);

		new TextPanel(ENTRANCE_LOCATION_1, ChatColor.GRAY + "Sewer Entrance").setVisible(true);
		new TextPanel(ENTRANCE_LOCATION_2, ChatColor.GRAY + "Flinton Sewers").setVisible(true);
	}

	private void setUpSludge() {
		sludge = new Source() {
			@Override
			public String getName() {
				return ChatColor.RED + "Sludge";
			}
		};
		new RepeatingTask(0.5) {
			boolean spawnOoze = true;

			@Override
			protected void run() {
				spawnOoze = !spawnOoze;
				Collider[] colliders = innerBounds.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof PlayerCharacterCollider) {
						PlayerCharacter pc = ((PlayerCharacterCollider) collider).getCharacter();
						Location location = pc.getLocation();
						World world = location.getWorld();
						Location floorLocation = location.clone().subtract(0, 1, 0);
						Block current = world.getBlockAt(location);
						Block blockBelow = world.getBlockAt(floorLocation);
						if (current.getType() == Material.AIR && blockBelow.getType() == Material.LIME_STAINED_GLASS) {
							pc.damage(SLUDGE_DAMAGE, sludge);
							Player player = pc.getPlayer();
							player.addPotionEffect(SLUDGE_SLOW_EFFECT);
							new RepeatingTask(0.1) {
								double count = 0;

								@Override
								protected void run() {
									Vector velocity = player.getVelocity();
									velocity.setY(-1);
									player.setVelocity(velocity);
									count++;
									if (count == 20) {
										cancel();
									}
								}
							}.schedule();
							SLUDGE_DAMAGE_NOISE.play(floorLocation);
							if (spawnOoze && smallGelatinousCubeCounts
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
		for (Location location : RAT_LOCATIONS) {
			new FlintonSewersRat(location).setAlive(true);
		}
		for (Location location : GELATINOUS_CCUBE_LOCATIONS) {
			new GelatinousCube(location).setAlive(true);
		}
		for (Location location : COLOSSAL_GELATINOUS_CUBE_LOCATIONS) {
			new ColossalGelatinousCube(location).setAlive(true);
		}
		for (Location location : BANDIT_LOCATIONS) {
			new Bandit(location).setAlive(true);
		}
		for (Location location : CULTIST_MAGE_LOCATOINS) {
			new CultistMage(location).setAlive(true);
		}
		for (Location location : CULTIST_SUMMONER_LOCATIONS) {
			new CultistSummoner(location).setAlive(true);
		}
		for (int i = 0; i < SACRIFICE_LOCATIONS.length; i++) {
			Location location = SACRIFICE_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new FlintonSewersCultSacrifice(location, male).setAlive(true);
		}
		new FlintonSewersXylphanos(XYLPHANOS_LOCATION).setAlive(true);
	}

	private void setUpPortcullises() {
		Location eastPortcullisLocation = new Location(Worlds.ELADRADOR, -126.5, 43, 44.5);
		Location westPortcullisLocation = new Location(Worlds.ELADRADOR, -374.5, 43, 89);
		Location northPortcullisLocation = new Location(Worlds.ELADRADOR, -314.5, 43, 0);
		new FlintonSewersPortcullis(eastPortcullisLocation, false, Items.EAST_SEWERS_KEY);
		new FlintonSewersPortcullis(westPortcullisLocation, false, Items.WEST_SEWERS_KEY);
		new FlintonSewersPortcullis(northPortcullisLocation, false, Items.NORTH_SEWERS_KEY);
	}

	private void placeLootChests() {
		for (Location location : LOOT_CHEST_LOCATIONS) {
			LootChest.spawnLootChest(location, CardinalDirection.SOUTH, 5);
		}
	}

}
