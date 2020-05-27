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
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.ui.TitleText;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Adventurer;
import com.mcmmorpg.impl.npcs.ColossalGelatinousCube;
import com.mcmmorpg.impl.npcs.CultistMage;
import com.mcmmorpg.impl.npcs.CultistSummoner;
import com.mcmmorpg.impl.npcs.FlintonSewersAlchemist;
import com.mcmmorpg.impl.npcs.FlintonSewersBandit;
import com.mcmmorpg.impl.npcs.FlintonSewersBanditChief;
import com.mcmmorpg.impl.npcs.FlintonSewersCultSacrifice;
import com.mcmmorpg.impl.npcs.FlintonSewersRat;
import com.mcmmorpg.impl.npcs.FlintonSewersXylphanos;
import com.mcmmorpg.impl.npcs.GelatinousCube;
import com.mcmmorpg.impl.npcs.GuardNadia;
import com.mcmmorpg.impl.npcs.GuardThomas;
import com.mcmmorpg.impl.npcs.SmallGelatinousCube;

public class FlintonSewersListener implements Listener {

	private static final double SLUDGE_TICK_DAMAGE = 2;
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
	private static final Location GUARD_NADIA_LOCATION = new Location(Worlds.ELADRADOR, -288.655881, 43.000000,
			24.776586, 341.285797f, 49.350960f);
	private static final Location GUARD_THOMAS_LOCATION = new Location(Worlds.ELADRADOR, -286.187316, 43.000000,
			25.479414, 14.596418f, 42.747627f);
	private static final Location[] ADVENTURER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -283.220539, 42.500000, 69.581283, -29.541777f, 25.022989f),
			new Location(Worlds.ELADRADOR, -264.001247, 42.500000, 79.596785, 191.547241f, 20.193523f),
			new Location(Worlds.ELADRADOR, -289.408052, 43.000000, 29.271209, 315.324951f, 29.504995f),
			new Location(Worlds.ELADRADOR, -285.484066, 43.000000, 28.261551, 6.412842f, 18.072203f),
			new Location(Worlds.ELADRADOR, -360.449712, 43.000000, 64.460531, 181.571655f, 41.357449f),
			new Location(Worlds.ELADRADOR, -364.974173, 43.000000, 64.404108, 181.571655f, 41.357449f),
			new Location(Worlds.ELADRADOR, -274.542431, 42.500000, 49.999362, 163.343491f, 31.278784f),
			new Location(Worlds.ELADRADOR, -276.569870, 42.500000, 109.014945, -70.951630f, 33.364014f),
			new Location(Worlds.ELADRADOR, -316.701861, 42.500000, 91.748185, -148.106323f, 42.400116f),
			new Location(Worlds.ELADRADOR, -304.604724, 42.500000, 48.562879, -124.126617f, 31.278788f),
			new Location(Worlds.ELADRADOR, -279.839748, 42.500000, 41.056248, -337.811829f, 19.462366f) };
	private static final Location[] RAT_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -232.826374, 42.500000, 85.681498, -38.950882f, 18.419712f),
			new Location(Worlds.ELADRADOR, -201.771779, 42.500000, 93.330063, -122.013359f, 18.767254f),
			new Location(Worlds.ELADRADOR, -217.072168, 42.500000, 64.801744, -287.442596f, 33.711544f),
			new Location(Worlds.ELADRADOR, -236.239407, 42.500000, 44.505452, -194.996643f, 35.101738f),
			new Location(Worlds.ELADRADOR, -181.135682, 42.500000, 47.657743, -96.348114f, 23.285328f),
			new Location(Worlds.ELADRADOR, -198.534969, 42.500000, 20.646020, -141.876190f, 19.462376f),
			new Location(Worlds.ELADRADOR, -172.227788, 42.500000, 0.309531, -150.564423f, 22.590256f),
			new Location(Worlds.ELADRADOR, -136.708584, 42.500000, 3.213595, -84.183899f, 41.357506f),
			new Location(Worlds.ELADRADOR, -143.381152, 42.500000, 55.777781, -325.324585f, 20.852547f),
			new Location(Worlds.ELADRADOR, -138.480258, 42.500000, 95.974000, -167.246033f, 14.596796f),
			new Location(Worlds.ELADRADOR, -197.667875, 43.000000, 109.618388, -214.859558f, 34.754204f),
			new Location(Worlds.ELADRADOR, -197.118779, 43.000000, 104.435703, -214.859558f, 34.754204f),
			new Location(Worlds.ELADRADOR, -167.876098, 43.000000, 18.721304, -276.370483f, 33.711586f),
			new Location(Worlds.ELADRADOR, -166.896079, 43.000000, 22.053514, -276.370483f, 33.711586f),
			new Location(Worlds.ELADRADOR, -228.268572, 43.000000, 2.122855, -284.014160f, 27.455822f),
			new Location(Worlds.ELADRADOR, -212.792677, 43.000000, 74.329376, -272.492249f, 33.364037f),
			new Location(Worlds.ELADRADOR, -167.898509, 43.000000, 18.689827, -188.392990f, 33.350693f),
			new Location(Worlds.ELADRADOR, -165.444397, 43.000000, 15.851488, -188.392990f, 33.350693f),
			new Location(Worlds.ELADRADOR, -228.142169, 43.000000, -0.316867, -87.953308f, 33.698223f),
			new Location(Worlds.ELADRADOR, -229.959528, 43.000000, 3.416159, -87.953308f, 33.698223f) };
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
			new Location(Worlds.ELADRADOR, -180.115110, 42.000000, -1.162628, -288.277161f, 21.881897f),
			new Location(Worlds.ELADRADOR, -242.841548, 42.500000, 106.591189, -358.395691f, 40.662357f),
			new Location(Worlds.ELADRADOR, -207.393830, 42.500000, 92.633073, -240.979980f, 17.029558f),
			new Location(Worlds.ELADRADOR, -160.130770, 42.500000, 41.361770, -89.798950f, 26.413179f),
			new Location(Worlds.ELADRADOR, -152.977493, 42.500000, 19.643239, -166.257813f, 37.186974f),
			new Location(Worlds.ELADRADOR, -191.385103, 42.500000, -3.323757, -20.690308f, 26.413177f),
			new Location(Worlds.ELADRADOR, -205.587498, 42.500000, 6.114156, -0.880371f, 28.845970f),
			new Location(Worlds.ELADRADOR, -197.772038, 42.500000, 21.827634, -3.661621f, 35.449257f),
			new Location(Worlds.ELADRADOR, -209.735653, 42.500000, 56.307206, -264.959656f, 29.541046f),
			new Location(Worlds.ELADRADOR, -236.038673, 42.500000, 36.428775, -357.754150f, 35.796787f),
			new Location(Worlds.ELADRADOR, -129.980817, 42.500000, 56.843103, -14.787476f, 20.852491f),
			new Location(Worlds.ELADRADOR, -156.893950, 42.500000, 71.169588, -263.922424f, 17.377075f),
			new Location(Worlds.ELADRADOR, -182.075481, 42.500000, 82.429137, -23.823486f, 33.711517f),
			new Location(Worlds.ELADRADOR, -157.577617, 42.500000, 101.407450, -48.498413f, 41.357437f),
			new Location(Worlds.ELADRADOR, -137.925418, 42.500000, 113.935849, -163.881348f, 38.229561f),
			new Location(Worlds.ELADRADOR, -137.542198, 42.500000, 81.123624, -133.645752f, 42.052521f) };
	private static final Location[] COLOSSAL_GELATINOUS_CUBE_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -111.284247, 42.000000, 90.107313, -1.620275f, 81.672386f) };
	private static final Location[] BANDIT_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -214.104260, 43.000000, 71.410061, 308.000610f, 21.881725f),
			new Location(Worlds.ELADRADOR, -211.841394, 43.000000, 76.085353, 311.823486f, 20.839100f),
			new Location(Worlds.ELADRADOR, -216.050534, 43.000000, 70.868015, 288.190857f, 28.485016f),
			new Location(Worlds.ELADRADOR, -207.690150, 43.000000, 42.819952, 5.341919f, 31.265396f),
			new Location(Worlds.ELADRADOR, -210.028890, 43.000000, 40.223001, 134.627686f, 23.271940f),
			new Location(Worlds.ELADRADOR, -213.536770, 43.000000, 42.933686, 54.345459f, 25.704735f),
			new Location(Worlds.ELADRADOR, -227.394238, 43.000000, -0.791380, 291.715332f, 20.839163f),
			new Location(Worlds.ELADRADOR, -227.479227, 43.000000, 3.631943, 291.715332f, 20.839163f),
			new Location(Worlds.ELADRADOR, -166.426276, 43.000000, 19.912115, 142.672241f, 41.344139f),
			new Location(Worlds.ELADRADOR, -168.589332, 43.000000, 17.079059, 142.672241f, 41.344139f),
			new Location(Worlds.ELADRADOR, -195.954821, 43.000000, 112.152243, 275.081543f, 21.534315f),
			new Location(Worlds.ELADRADOR, -197.085622, 43.000000, 108.918592, 275.081543f, 21.534315f),
			new Location(Worlds.ELADRADOR, -194.574478, 43.000000, 105.422347, 275.081543f, 21.534315f),
			new Location(Worlds.ELADRADOR, -362.341809, 43.000000, 36.140411, 268.781372f, 17.363811f),
			new Location(Worlds.ELADRADOR, -363.652486, 43.000000, 33.009571, 268.781372f, 17.363811f),
			new Location(Worlds.ELADRADOR, -360.458331, 43.000000, 30.811628, 268.781372f, 17.363811f),
			new Location(Worlds.ELADRADOR, -365.548701, 43.000000, 36.509118, 50.230835f, 22.924479f),
			new Location(Worlds.ELADRADOR, -366.472401, 43.000000, 30.521944, 152.060669f, 18.406443f),
			new Location(Worlds.ELADRADOR, -363.165242, 43.000000, -3.257100, 298.078857f, 21.881847f),
			new Location(Worlds.ELADRADOR, -359.543529, 43.000000, -4.684050, 298.078857f, 21.881847f),
			new Location(Worlds.ELADRADOR, -358.938538, 43.000000, -1.179748, 298.078857f, 21.881847f),
			new Location(Worlds.ELADRADOR, -364.633046, 43.000000, -5.294538, 146.203125f, 33.698231f), };
	private static final Location[] BANDIT_CHIEF_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -196.819441, 43.000000, 108.587979, -78.763306f, 35.101715f),
			new Location(Worlds.ELADRADOR, -168.562343, 43.000000, 18.289364, -56.867340f, 18.072174f),
			new Location(Worlds.ELADRADOR, -226.945395, 43.000000, 1.310684, -271.992371f, 22.937742f),
			new Location(Worlds.ELADRADOR, -211.706564, 43.000000, 41.520396, -186.792236f, 17.029551f),
			new Location(Worlds.ELADRADOR, -213.347313, 43.000000, 74.206718, -287.927307f, -7.645914f),
			new Location(Worlds.ELADRADOR, -361.319396, 43.000000, -4.471523, -0.179626f, 29.875282f), };
	private static final Location[] CULTIST_MAGE_LOCATOINS = {
			new Location(Worlds.ELADRADOR, -408.327771, 42.000000, 56.875984, 154.889282f, 25.704796f),
			new Location(Worlds.ELADRADOR, -407.245511, 42.000000, 49.336538, 155.236816f, 25.704796f),
			new Location(Worlds.ELADRADOR, -364.974173, 43.000000, 64.404108, 181.571655f, 41.357449f),
			new Location(Worlds.ELADRADOR, -360.449712, 43.000000, 64.460531, 181.571655f, 41.357449f) };
	private static final Location[] CULTIST_SUMMONER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -413.452912, 42.000000, 52.049572, 91.239502f, 39.953979f),
			new Location(Worlds.ELADRADOR, -423.961213, 42.000000, 62.317555, 178.472473f, 77.836029f),
			new Location(Worlds.ELADRADOR, -434.167149, 42.000000, 52.071448, 269.180481f, 60.111427f),
			new Location(Worlds.ELADRADOR, -424.141625, 42.000000, 41.386090, 4.057861f, 47.252380f),
			new Location(Worlds.ELADRADOR, -364.516160, 43.000000, 58.550369, 310.110229f, 22.937761f),
			new Location(Worlds.ELADRADOR, -360.506589, 43.000000, 59.223588, 27.611450f, 26.065636f) };
	private static final Location[] SACRIFICE_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -428.389763, 46.000000, 54.862500, 164.465683f, 421.052555f),
			new Location(Worlds.ELADRADOR, -426.321504, 47.000000, 49.862500, 183.232819f, 84.800171f),
			new Location(Worlds.ELADRADOR, -422.642433, 49.000000, 52.862500, 176.977356f, 74.373940f) };
	private static final Location XYLPHANOS_LOCATION = new Location(Worlds.ELADRADOR, -424.004659, 42.000000, 51.943707,
			259.449280f, 79.573784f);
	private static final Location[] LOOT_CHEST_LOCATIONS = { new Location(Worlds.ELADRADOR, -204, 44, 70, 90, 0),
			new Location(Worlds.ELADRADOR, -208, 44, 49, 180, 0), new Location(Worlds.ELADRADOR, -220, 44, -3, 90, 0),
			new Location(Worlds.ELADRADOR, -159, 44, 14, 90, 0), new Location(Worlds.ELADRADOR, -188, 44, 104, 90, 0),
			new Location(Worlds.ELADRADOR, -354, 44, 57, 90, 0), new Location(Worlds.ELADRADOR, -354, 44, 29, 90, 0),
			new Location(Worlds.ELADRADOR, -357, 44, 4, 180, 0), new Location(Worlds.ELADRADOR, -273, 44, 9, 0, 0) };
	private static final Item[][] LOOT_CHEST_CONTENTS = { { Items.EAST_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING },
			{ Items.WEST_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING },
			{ Items.EAST_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING },
			{ Items.NORTH_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING, Items.POTION_OF_LESSER_HEALING },
			{ Items.NORTH_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING },
			{ Items.WEST_SEWERS_KEY, Items.POTION_OF_HEALING },
			{ Items.NORTH_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING },
			{ Items.NORTH_SEWERS_KEY, Items.POTION_OF_LESSER_HEALING }, { Items.SKELETAL_WAND, Items.BATTLE_AXE,
					Items.POTION_OF_LESSER_HEALING, Items.POTION_OF_LESSER_HEALING, Items.POTION_OF_HEALING } };

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
		new RepeatingTask(0.1) {
			int count = 0;

			@Override
			protected void run() {
				boolean spawnOoze = count == 10;
				boolean playSludgeSound = count % 5 == 0;
				if (spawnOoze) {
					count = 0;
				}
				count++;
				Collider[] colliders = innerBounds.getCollidingColliders();
				for (Collider collider : colliders) {
					if (collider instanceof PlayerCharacterCollider) {
						PlayerCharacter pc = ((PlayerCharacterCollider) collider).getCharacter();
						Location location = pc.getLocation();
						World world = location.getWorld();
						Block current = world.getBlockAt(location);
						if (current.getType() == Material.AIR) {
							Location floorLocation = location.clone().subtract(0, 1, 0);
							Block blockBelow = world.getBlockAt(floorLocation);
							if (blockBelow.getType() == Material.LIME_STAINED_GLASS) {
								pc.damage(SLUDGE_TICK_DAMAGE, sludge);
								Player player = pc.getPlayer();
								player.addPotionEffect(SLUDGE_SLOW_EFFECT);
								Vector velocity = player.getVelocity();
								velocity.setY(-1);
								player.setVelocity(velocity);
								if (playSludgeSound) {
									SLUDGE_DAMAGE_NOISE.play(floorLocation);
								}
								if (spawnOoze && smallGelatinousCubeCounts
										.get(pc) < MAX_SMALL_GELATINOUS_CUBE_COUNT_PER_PLAYER_CHARACTER) {
									Location spawnLocation = pc.getLocation();
									new SmallGelatinousCube(spawnLocation, pc).setAlive(true);
									pc.sendMessage(ChatColor.GRAY + "A monster came out of the sludge!");
								}
							}
						}
					}
				}
			}
		}.schedule();
	}

	private void spawnNpcs() {
		new FlintonSewersAlchemist(ALCHEMIST_LOCATION).setAlive(true);
		new GuardNadia(GUARD_NADIA_LOCATION).setAlive(true);
		new GuardThomas(GUARD_THOMAS_LOCATION).setAlive(true);
		for (int i = 0; i < ADVENTURER_LOCATIONS.length; i++) {
			Location location = ADVENTURER_LOCATIONS[i];
			boolean male = Math.random() < 0.5;
			new Adventurer(location, male).setAlive(true);
		}
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
			new FlintonSewersBandit(location).setAlive(true);
		}
		for (Location location : BANDIT_CHIEF_LOCATIONS) {
			new FlintonSewersBanditChief(location).setAlive(true);
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
		for (int i = 0; i < LOOT_CHEST_LOCATIONS.length; i++) {
			Location location = LOOT_CHEST_LOCATIONS[i];
			Item[] contents = LOOT_CHEST_CONTENTS[i];
			LootChest.spawnLootChest(location, 5, contents);
		}
	}

}
