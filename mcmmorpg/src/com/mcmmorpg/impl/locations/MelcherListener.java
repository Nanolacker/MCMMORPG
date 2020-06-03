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
import com.mcmmorpg.impl.RespawnLocations;
import com.mcmmorpg.impl.Soundtracks;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.Zones;
import com.mcmmorpg.impl.npcs.Adventurer;
import com.mcmmorpg.impl.npcs.Broodmother;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.ForestSpider;
import com.mcmmorpg.impl.npcs.Guard;
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
			new Location(Worlds.ELADRADOR, -1094, 70, 202), new Location(Worlds.ELADRADOR, -1089, 70, 202),
			new Location(Worlds.ELADRADOR, -1160.519999, 73.000000, 260.705379, -84.450874f, 27.899969f),
			new Location(Worlds.ELADRADOR, -1145.512238, 73.000000, 253.067318, -108.151115f, 36.450005f),
			new Location(Worlds.ELADRADOR, -1125.251013, 73.000000, 237.489915, -81.750854f, 41.850060f),
			new Location(Worlds.ELADRADOR, -1092.976828, 70.000000, 228.970498, -106.501106f, 47.700119f),
			new Location(Worlds.ELADRADOR, -1053.620191, 70.000000, 201.375376, -64.951118f, -21.899975f),
			new Location(Worlds.ELADRADOR, -1029.301359, 70.000000, 199.677786, -121.501740f, 32.700016f),
			new Location(Worlds.ELADRADOR, -1002.244331, 70.000000, 194.330394, -75.001625f, 39.299908f),
			new Location(Worlds.ELADRADOR, -970.673039, 72.000000, 153.875745, -13.502177f, 34.199734f) };
	private static final Location[] LUMBERJACK_LOCATIONS = { new Location(Worlds.ELADRADOR, -1049, 70, 232, 225, 0) };
	private static final Location[] GUARD_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -810.359752, 72.000000, 152.342278, 227.305908f, 30.583672f),
			new Location(Worlds.ELADRADOR, -807.721378, 79.000000, 159.099585, 152.584518f, 39.606464f),
			new Location(Worlds.ELADRADOR, -808.476754, 79.000000, 163.233276, 137.292969f, 30.917925f),
			new Location(Worlds.ELADRADOR, -985.106391, 71.000000, 171.145248, -2.070502f, 25.357178f),
			new Location(Worlds.ELADRADOR, -1020.366626, 70.000000, 184.174917, -2.765596f, 21.186670f),
			new Location(Worlds.ELADRADOR, -1047.762097, 70.000000, 210.118691, -92.431229f, 21.534216f),
			new Location(Worlds.ELADRADOR, -1086.575005, 70.000000, 218.394587, -7.631108f, 31.265347f),
			new Location(Worlds.ELADRADOR, -1090.369521, 70.000000, 197.377583, 5.923175f, 33.003071f),
			new Location(Worlds.ELADRADOR, -1128.867352, 73.000000, 240.718627, 167.182632f, 23.271896f) };
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
	private static final Location[] THIEF_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -1156.153368, 79.000000, 143.764574, -2.819763f, 23.632837f),
			new Location(Worlds.ELADRADOR, -1149.941193, 79.000000, 145.558699, -2.819763f, 23.632837f),
			new Location(Worlds.ELADRADOR, -1144.558112, 79.000000, 145.438248, -2.819763f, 23.632837f),
			new Location(Worlds.ELADRADOR, -1144.909828, 79.000000, 141.542111, -2.819763f, 23.632837f),
			new Location(Worlds.ELADRADOR, -1148.892365, 79.000000, 139.875135, -248.131317f, 33.711540f),
			new Location(Worlds.ELADRADOR, -1154.496256, 79.000000, 137.626322, -248.131317f, 33.711540f),
			new Location(Worlds.ELADRADOR, -1142.560511, 79.000000, 137.792785, -19.501740f, 35.449249f),
			new Location(Worlds.ELADRADOR, -1139.940007, 78.000000, 142.938132, -19.501740f, 35.449249f),
			new Location(Worlds.ELADRADOR, -1180.917637, 74.000000, 155.156277, -348.569916f, 11.468884f),
			new Location(Worlds.ELADRADOR, -1188.227450, 74.000000, 162.177680, -6.989624f, 15.986921f),
			new Location(Worlds.ELADRADOR, -1118.575813, 76.000000, 155.300048, -15.678314f, 26.413179f),
			new Location(Worlds.ELADRADOR, -1109.433998, 77.000000, 155.343152, -32.707916f, 26.760717f) };
	private static final Location[] HIGHWAYMAN_LOCATIONS = { new Location(Worlds.ELADRADOR, -756, 72, 147),
			new Location(Worlds.ELADRADOR, -727, 71, 88), new Location(Worlds.ELADRADOR, -684, 65, 81),
			new Location(Worlds.ELADRADOR, -680.297733, 64.000000, 74.673399, 317.667206f, 17.724596f),
			new Location(Worlds.ELADRADOR, -727.586013, 71.062500, 94.653988, 137.292496f, 16.682007f),
			new Location(Worlds.ELADRADOR, -731.999106, 71.062500, 95.228533, 155.364761f, 15.291833f),
			new Location(Worlds.ELADRADOR, -630.226817, 67.000000, 66.887372, 199.503250f, 22.242643f),
			new Location(Worlds.ELADRADOR, -646.951926, 68.000000, 26.681379, 147.024582f, 21.895102f),
			new Location(Worlds.ELADRADOR, -637.849987, 71.000000, -34.146066, 210.971893f, 20.504936f),
			new Location(Worlds.ELADRADOR, -570.910808, 75.000000, -79.454968, 230.434174f, 22.590183f),
			new Location(Worlds.ELADRADOR, -516.452902, 74.000000, -113.060670, 318.361694f, 24.675428f),
			new Location(Worlds.ELADRADOR, -465.374197, 73.000000, -84.707907, 334.695587f, 36.491856f),
			new Location(Worlds.ELADRADOR, -427.140385, 78.000000, -32.814684, 336.085419f, 23.980362f), };
	private static final Location[] FOREST_SPIDER_LOCATIONS = {
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
			new Location(Worlds.ELADRADOR, -814.411626, 74.000000, -99.851284, 287.417755f, 28.150875f),
			new Location(Worlds.ELADRADOR, -668.392343, 65.000000, 30.213392, 89.679626f, 19.809883f),
			new Location(Worlds.ELADRADOR, -679.518897, 64.000000, 38.589283, 89.679626f, 19.809883f),
			new Location(Worlds.ELADRADOR, -688.146605, 65.000000, 30.011950, 117.482971f, 16.682007f),
			new Location(Worlds.ELADRADOR, -703.465234, 67.000000, 22.065936, 131.037064f, 25.370546f),
			new Location(Worlds.ELADRADOR, -707.048418, 69.000000, 4.114079, 133.817444f, 24.327919f),
			new Location(Worlds.ELADRADOR, -725.442575, 72.000000, -8.344703, 143.200882f, 33.711548f),
			new Location(Worlds.ELADRADOR, -731.580913, 74.000000, -19.842024, 119.915619f, 24.327930f),
			new Location(Worlds.ELADRADOR, -747.299626, 77.000000, -34.634385, 125.128838f, 33.711559f),
			new Location(Worlds.ELADRADOR, -763.939928, 78.000000, -45.860363, 144.591141f, 31.973850f),
			new Location(Worlds.ELADRADOR, -776.522285, 78.000000, -62.876873, 144.591141f, 31.973850f),
			new Location(Worlds.ELADRADOR, -807.730458, 75.000000, -76.742250, 133.122330f, 34.059101f),
			new Location(Worlds.ELADRADOR, -820.681832, 62.000000, -117.208008, 351.377838f, 26.399847f),
			new Location(Worlds.ELADRADOR, -821.692052, 62.000000, -121.906648, 181.430283f, 35.088398f),
			new Location(Worlds.ELADRADOR, -819.905641, 62.000000, -126.486904, 343.384521f, 16.668697f),
			new Location(Worlds.ELADRADOR, -821.175368, 69.062500, -102.003955, 189.076080f, 62.891785f),
			new Location(Worlds.ELADRADOR, -837.956110, 69.062500, -126.201293, 273.876038f, 24.314703f),
			new Location(Worlds.ELADRADOR, -823.745960, 63.062500, -110.309364, 197.121582f, 21.186844f),
			new Location(Worlds.ELADRADOR, -825.661351, 61.062500, -129.120002, 306.597290f, 22.577007f),
			new Location(Worlds.ELADRADOR, -807.805341, 61.437500, -123.851920, 68.931091f, 4.852366f),
			new Location(Worlds.ELADRADOR, -811.313284, 74.000000, -81.733326, 336.833496f, 19.449106f), };
	private static final Location BROODMOTHER_LOCATION = new Location(Worlds.ELADRADOR, -811.832828, 62.000000,
			-123.009372, 79.294250f, 10.760525f);
	private static final Location GUARD_JAMES_LOCATION = new Location(Worlds.ELADRADOR, -643.730402, 68.000000,
			22.268770, 82.717430f, 74.708183f);

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
					pc.setRespawnLocation(RespawnLocations.MELCHER);
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
		for (int i = 0; i < GUARD_LOCATIONS.length; i++) {
			Location location = GUARD_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new Guard(location, male).setAlive(true);
		}
		new MelcherMayor(MAYOR_LOCATION).setAlive(true);
		new MelcherFarmer(FARMER_LOCATION).setAlive(true);
		new MelcherBartender(BARTENDER_LOCATION).setAlive(true);
		new MelcherAngeredDrunkard(DRUNKARD_LOCATION).setAlive(true);
		for (Location location : TRAINING_DUMMY_LOCATIONS) {
			new TrainingDummy(location).setAlive(true);
		}
		for (int i = 0; i < ADVENTURER_LOCATIONS.length; i++) {
			Location location = ADVENTURER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new Adventurer(location, male).setAlive(true);
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
