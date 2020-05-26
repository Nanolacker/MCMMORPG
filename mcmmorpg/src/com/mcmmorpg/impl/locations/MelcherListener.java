package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.Quests;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Adventurer;
import com.mcmmorpg.impl.npcs.Broodmother;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.ForestSpider;
import com.mcmmorpg.impl.npcs.GuardJames;
import com.mcmmorpg.impl.npcs.Highwayman;
import com.mcmmorpg.impl.npcs.Horse;
import com.mcmmorpg.impl.npcs.MelcherAngeredDrunkard;
import com.mcmmorpg.impl.npcs.MelcherBartender;
import com.mcmmorpg.impl.npcs.MelcherFarmer;
import com.mcmmorpg.impl.npcs.MelcherLumberjack;
import com.mcmmorpg.impl.npcs.MelcherMayor;
import com.mcmmorpg.impl.npcs.MelcherTavernKingRat;
import com.mcmmorpg.impl.npcs.MelcherTavernRat;
import com.mcmmorpg.impl.npcs.MelcherVillager;
import com.mcmmorpg.impl.npcs.Thief;
import com.mcmmorpg.impl.npcs.TrainingDummy;

public class MelcherListener implements Listener {

	private static final Location PLAYER_CHARACTER_RESPAWN_LOCATION = new Location(Worlds.ELADRADOR, -1019, 70, 191);
	private static final Location[] VILLAGER_LOCATIONS = { new Location(Worlds.ELADRADOR, -1167, 73, 273),
			new Location(Worlds.ELADRADOR, -1153, 73, 269), new Location(Worlds.ELADRADOR, -1138, 73, 260),
			new Location(Worlds.ELADRADOR, -1146, 73, 240), new Location(Worlds.ELADRADOR, -1130, 73, 242),
			new Location(Worlds.ELADRADOR, -1113, 72, 231), new Location(Worlds.ELADRADOR, -1100, 70, 218),
			new Location(Worlds.ELADRADOR, -1086, 70, 233), new Location(Worlds.ELADRADOR, -1073, 70, 211),
			new Location(Worlds.ELADRADOR, -1058, 70, 228), new Location(Worlds.ELADRADOR, -1037, 70, 233),
			new Location(Worlds.ELADRADOR, -1025, 70, 207), new Location(Worlds.ELADRADOR, -1030, 70, 185),
			new Location(Worlds.ELADRADOR, -1032, 70, 170), new Location(Worlds.ELADRADOR, -1013, 70, 181),
			new Location(Worlds.ELADRADOR, -998, 70, 208), new Location(Worlds.ELADRADOR, -988, 71, 181),
			new Location(Worlds.ELADRADOR, -969, 71, 177), new Location(Worlds.ELADRADOR, -957, 72, 157),
			new Location(Worlds.ELADRADOR, -929, 72, 155), new Location(Worlds.ELADRADOR, -1082, 70, 242),
			new Location(Worlds.ELADRADOR, -1073, 70, 241), new Location(Worlds.ELADRADOR, -1079, 75, 240),
			new Location(Worlds.ELADRADOR, -1094, 70, 202), new Location(Worlds.ELADRADOR, -1089, 70, 202) };
	private static final Location[] LUMBERJACK_LOCATIONS = { new Location(Worlds.ELADRADOR, -1049, 70, 232, 225, 0) };
	private static final Location MAYOR_LOCATION = new Location(Worlds.ELADRADOR, -1092.5, 70, 197.5);
	private static final Location FARMER_LOCATION = new Location(Worlds.ELADRADOR, -1166, 73, 246);
	private static final Location BARTENDER_LOCATION = new Location(Worlds.ELADRADOR, -1086.5, 70, 243.5, -90, 0);
	private static final Location DRUNKARD_LOCATION = new Location(Worlds.ELADRADOR, -1075, 70, 243);
	private static final Location[] TRAINING_DUMMY_LOCATIONS = { new Location(Worlds.ELADRADOR, -1115, 73, 249, 90, 0),
			new Location(Worlds.ELADRADOR, -1117, 73, 251, 90, 0),
			new Location(Worlds.ELADRADOR, -1116, 73, 255, -45, 0),
			new Location(Worlds.ELADRADOR, -1114, 73, 252, 90, 0),
			new Location(Worlds.ELADRADOR, -1118, 73, 248, 90, 0),
			new Location(Worlds.ELADRADOR, -1119, 73, 256, 180, 0) };
	private static final Location[] ADVENTURER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -1122.425572, 73.000000, 261.720727, 179.691650f, 22.590204f),
			new Location(Worlds.ELADRADOR, -1126.663995, 72.937500, 253.256357, 178.649048f, 23.980371f),
			new Location(Worlds.ELADRADOR, -1120.104211, 73.000000, 250.863035, 239.816299f, 13.554127f),
			new Location(Worlds.ELADRADOR, -878.337963, 72.000000, 164.330094, -2.471985f, 26.413113f),
			new Location(Worlds.ELADRADOR, -823.522638, 72.000000, 153.239366, -181.107422f, 67.770515f),
			new Location(Worlds.ELADRADOR, -756.885689, 72.000000, 121.383997, -75.107544f, 41.704929f),
			new Location(Worlds.ELADRADOR, -649.650656, 64.000000, 79.879587, -353.435791f, 41.009830f),
			new Location(Worlds.ELADRADOR, -651.847714, 67.000000, 38.008869, -150.176498f, 21.199955f),
			new Location(Worlds.ELADRADOR, -585.913308, 74.000000, -77.914642, -6.642093f, 30.236000f),
			new Location(Worlds.ELADRADOR, -469.161661, 73.000000, -103.509018, -291.626099f, 46.570435f),
			new Location(Worlds.ELADRADOR, -404.779515, 82.000000, -11.314008, -280.852203f, 22.242554f) };
	private static final Location[] CHICKEN_LOCATIONS = { new Location(Worlds.ELADRADOR, -1167, 73, 243),
			new Location(Worlds.ELADRADOR, -1167, 73, 242), new Location(Worlds.ELADRADOR, -1167, 73, 241) };
	private static final Location[] HORSE_LOCATIONS = { new Location(Worlds.ELADRADOR, -1158, 73, 239),
			new Location(Worlds.ELADRADOR, -1158, 73, 244), new Location(Worlds.ELADRADOR, -1158, 73, 249) };
	private static final Location[] TAVERN_RAT_LOCATIONS = { new Location(Worlds.ELADRADOR, -1086, 65, 240),
			new Location(Worlds.ELADRADOR, -1084, 65, 242), new Location(Worlds.ELADRADOR, -1082, 65, 245),
			new Location(Worlds.ELADRADOR, -1079, 65, 245), new Location(Worlds.ELADRADOR, -1079, 65, 241),
			new Location(Worlds.ELADRADOR, -1074, 65, 244), new Location(Worlds.ELADRADOR, -1072, 65, 240) };
	private static final Location TAVERN_KING_RAT_LOCATION = new Location(Worlds.ELADRADOR, -1072, 65, 242);
	private static final Location[] THIEF_LOCATIONS = { new Location(Worlds.ELADRADOR, -1157, 74, 173),
			new Location(Worlds.ELADRADOR, -1150, 79, 147), new Location(Worlds.ELADRADOR, -1043, 74, 115),
			new Location(Worlds.ELADRADOR, 981, 75, 111), new Location(Worlds.ELADRADOR, -919, 74, 123) };
	private static final Location[] HIGHWAYMAN_LOCATIONS = { new Location(Worlds.ELADRADOR, -813, 72, 151),
			new Location(Worlds.ELADRADOR, -785, 72, 158), new Location(Worlds.ELADRADOR, -756, 72, 147),
			new Location(Worlds.ELADRADOR, -727, 71, 88), new Location(Worlds.ELADRADOR, -684, 65, 81),
			new Location(Worlds.ELADRADOR, -680.297733, 64.000000, 74.673399, 317.667206f, 17.724596f),
			new Location(Worlds.ELADRADOR, -630.226817, 67.000000, 66.887372, 199.503250f, 22.242643f),
			new Location(Worlds.ELADRADOR, -646.951926, 68.000000, 26.681379, 147.024582f, 21.895102f),
			new Location(Worlds.ELADRADOR, -637.849987, 71.000000, -34.146066, 210.971893f, 20.504936f),
			new Location(Worlds.ELADRADOR, -570.910808, 75.000000, -79.454968, 230.434174f, 22.590183f),
			new Location(Worlds.ELADRADOR, -516.452902, 74.000000, -113.060670, 318.361694f, 24.675428f),
			new Location(Worlds.ELADRADOR, -465.374197, 73.000000, -84.707907, 334.695587f, 36.491856f),
			new Location(Worlds.ELADRADOR, -427.140385, 78.000000, -32.814684, 336.085419f, 23.980362f), };
	private static final Location[] FOREST_SPIDER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -815.000000, 71.000000, 159.000000),
			new Location(Worlds.ELADRADOR, -792.000000, 73.000000, 147.000000),
			new Location(Worlds.ELADRADOR, -763.000000, 72.000000, 155.000000),
			new Location(Worlds.ELADRADOR, -750.000000, 72.000000, 100.000000),
			new Location(Worlds.ELADRADOR, -715.000000, 70.000000, 90.000000),
			new Location(Worlds.ELADRADOR, -630.000000, 66.000000, 74.000000),
			new Location(Worlds.ELADRADOR, -626.000000, 68.000000, 41.000000),
			new Location(Worlds.ELADRADOR, -656.000000, 68.000000, 13.000000),
			new Location(Worlds.ELADRADOR, -646.000000, 70.000000, -22.000000),
			new Location(Worlds.ELADRADOR, -622.000000, 73.000000, -45.000000),
			new Location(Worlds.ELADRADOR, -599.626059, 75.000000, -68.358134, 240.513687f, 21.895067f),
			new Location(Worlds.ELADRADOR, -545.113880, 75.000000, -106.429543, 231.825058f, 11.816359f),
			new Location(Worlds.ELADRADOR, -456.903532, 72.000000, -62.710678, 329.483612f, 15.291774f),
			new Location(Worlds.ELADRADOR, -813.256476, 74.000000, -93.550042, 5.561591f, 25.718084f),
			new Location(Worlds.ELADRADOR, -819.643090, 74.000000, -91.068068, 5.561591f, 25.718084f),
			new Location(Worlds.ELADRADOR, -822.335181, 74.000000, -86.354005, 5.561591f, 25.718084f),
			new Location(Worlds.ELADRADOR, -823.469786, 74.000000, -80.915253, 5.561591f, 25.718084f),
			new Location(Worlds.ELADRADOR, -828.272300, 74.000000, -85.618514, 160.565094f, 20.157419f),
			new Location(Worlds.ELADRADOR, -826.661654, 74.000000, -92.967181, 203.312759f, 19.114796f),
			new Location(Worlds.ELADRADOR, -822.018759, 74.000000, -100.451275, 226.598053f, 19.809881f),
			new Location(Worlds.ELADRADOR, -814.411626, 74.000000, -99.851284, 287.417755f, 28.150875f) };
	private static final Location BROODMOTHER_LOCATION = new Location(Worlds.ELADRADOR, -813.208736, 74.000000,
			-83.282707, -202.963303f, 1.390176f);
	private static final Location GUARD_JAMES_LOCATION = new Location(Worlds.ELADRADOR, -837, 72, 148);

	public MelcherListener() {
		setBounds();
		setUpNpcs();
	}

	private void setBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, -1186, 30, 110, -930, 126, 300) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.MELCHER);
					pc.setRespawnLocation(PLAYER_CHARACTER_RESPAWN_LOCATION);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.VILLAGE);
				}
			}
		};
		entranceBounds.setActive(true);
		Collider exitBounds = new Collider(Worlds.ELADRADOR, -1191, 25, 105, -925, 131, 305) {
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

	private void setUpNpcs() {
		for (int i = 0; i < VILLAGER_LOCATIONS.length; i++) {
			Location location = VILLAGER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new MelcherVillager(location, male).setAlive(true);
		}
		for (Location location : LUMBERJACK_LOCATIONS) {
			new MelcherLumberjack(location).setAlive(true);
		}
		new MelcherMayor(MAYOR_LOCATION).setAlive(true);
		new MelcherFarmer(FARMER_LOCATION).setAlive(true);
		new MelcherBartender(BARTENDER_LOCATION).setAlive(true);
		new MelcherAngeredDrunkard(DRUNKARD_LOCATION).setAlive(true);
		for (Location location : TRAINING_DUMMY_LOCATIONS) {
			new TrainingDummy(location).setAlive(true);
		}
		for (Location location : ADVENTURER_LOCATIONS) {
			new Adventurer(location).setAlive(true);
		}
		for (Location location : CHICKEN_LOCATIONS) {
			new Chicken(location).setAlive(true);
		}
		for (Location location : HORSE_LOCATIONS) {
			new Horse(ChatColor.GREEN + "Horse", 3, location).setAlive(true);
		}
		for (Location location : TAVERN_RAT_LOCATIONS) {
			new MelcherTavernRat(location).setAlive(true);
		}
		new MelcherTavernKingRat(TAVERN_KING_RAT_LOCATION).setAlive(true);
		for (Location location : THIEF_LOCATIONS) {
			new Thief(location).setAlive(true);
		}
		for (Location location : HIGHWAYMAN_LOCATIONS) {
			new Highwayman(location).setAlive(true);
		}
		for (Location location : FOREST_SPIDER_LOCATIONS) {
			new ForestSpider(location).setAlive(true);
		}
		new Broodmother(BROODMOTHER_LOCATION).setAlive(true);
		new GuardJames(GUARD_JAMES_LOCATION).setAlive(true);
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		int level = event.getNewLevel();
		if (level == 1) {
			PlayerCharacter pc = event.getPlayerCharacter();
			Quests.REPORTING_FOR_DUTY.start(pc);
		}
	}

}
