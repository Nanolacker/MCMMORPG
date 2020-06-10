package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.RespawnLocations;
import com.mcmmorpg.impl.constants.Soundtracks;
import com.mcmmorpg.impl.constants.Worlds;
import com.mcmmorpg.impl.constants.Zones;
import com.mcmmorpg.impl.npcs.Adventurer;
import com.mcmmorpg.impl.npcs.Broodmother;
import com.mcmmorpg.impl.npcs.ForestSpider;
import com.mcmmorpg.impl.npcs.GuardJames;
import com.mcmmorpg.impl.npcs.Highwayman;

/**
 * Listener for the forest east of Melcher and west of Flinton that also spawns
 * NPCs.
 */
public class ForestListener implements Listener {

	private static final double LOOT_CHEST_RESPAWN_TIME = 60;
	private static final Location[] ADVENTURER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -756.885689, 72.000000, 121.383997, -75.107544f, 41.704929f),
			new Location(Worlds.ELADRADOR, -649.650656, 64.000000, 79.879587, -353.435791f, 41.009830f),
			new Location(Worlds.ELADRADOR, -651.847714, 67.000000, 38.008869, -150.176498f, 21.199955f),
			new Location(Worlds.ELADRADOR, -469.161661, 73.000000, -103.509018, -291.626099f, 46.570435f),
			new Location(Worlds.ELADRADOR, -404.779515, 82.000000, -11.314008, -280.852203f, 22.242554f) };
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
			new Location(Worlds.ELADRADOR, -427.140385, 78.000000, -32.814684, 336.085419f, 23.980362f),
			new Location(Worlds.ELADRADOR, -596.675401, 74.000000, -77.495592, 18.078186f, 14.235965f),
			new Location(Worlds.ELADRADOR, -604.684367, 75.062500, -79.492789, 18.773926f, 19.449081f),
			new Location(Worlds.ELADRADOR, -608.780557, 75.000000, -87.723093, 334.289001f, 25.704823f),
			new Location(Worlds.ELADRADOR, -597.679685, 74.000000, -82.694009, 14.603943f, 35.435978f),
			new Location(Worlds.ELADRADOR, -576.986391, 75.000000, -87.578919, 351.667786f, 42.386822f),
			new Location(Worlds.ELADRADOR, -574.679453, 75.062500, -92.631863, 17.734009f, 39.258965f),
			new Location(Worlds.ELADRADOR, -501.727386, 73.062500, -122.186359, -277.840179f, 34.767361f),
			new Location(Worlds.ELADRADOR, -507.477083, 73.000000, -120.535543, -277.840179f, 34.767361f),
			new Location(Worlds.ELADRADOR, -503.269467, 73.062500, -124.977532, 38.074974f, 35.809978f) };
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
			new Location(Worlds.ELADRADOR, -821.692052, 62.000000, -121.906648, 181.430283f, 35.088398f),
			new Location(Worlds.ELADRADOR, -819.905641, 62.000000, -126.486904, 343.384521f, 16.668697f),
			new Location(Worlds.ELADRADOR, -837.956110, 69.062500, -126.201293, 273.876038f, 24.314703f),
			new Location(Worlds.ELADRADOR, -825.661351, 61.062500, -129.120002, 306.597290f, 22.577007f),
			new Location(Worlds.ELADRADOR, -811.313284, 74.000000, -81.733326, 336.833496f, 19.449106f),
			new Location(Worlds.ELADRADOR, -795.917835, 75.000000, -109.388263, 166.872681f, 39.619694f),
			new Location(Worlds.ELADRADOR, -783.318384, 75.000000, -129.136806, 113.698822f, 40.314774f),
			new Location(Worlds.ELADRADOR, -806.446726, 74.000000, -142.362600, 116.131561f, 40.662315f),
			new Location(Worlds.ELADRADOR, -825.568039, 73.000000, -150.545925, 49.456543f, 27.108164f),
			new Location(Worlds.ELADRADOR, -845.281415, 75.000000, -120.567044, 17.135315f, 25.022921f),
			new Location(Worlds.ELADRADOR, -835.165201, 74.000000, -93.681517, 325.004578f, 32.668819f),
			new Location(Worlds.ELADRADOR, -771.130827, 78.000000, -54.103961, 297.896973f, 30.583574f),
			new Location(Worlds.ELADRADOR, -741.368754, 75.000000, -22.526936, 333.988647f, -27.803377f),
			new Location(Worlds.ELADRADOR, -721.434955, 73.000000, -16.228231, 347.890259f, 32.668839f),
			new Location(Worlds.ELADRADOR, -714.693233, 71.000000, 1.647090, 298.539551f, 38.229507f),
			new Location(Worlds.ELADRADOR, -703.347697, 67.000000, 20.112461, 347.542847f, 35.101631f),
			new Location(Worlds.ELADRADOR, -698.175510, 64.000000, 47.184498, 182.861328f, 25.370466f),
			new Location(Worlds.ELADRADOR, -695.052537, 64.000000, 55.789692, 217.962646f, 25.370470f),
			new Location(Worlds.ELADRADOR, -716.452455, 66.000000, 37.016079, 172.781921f, 29.888496f),
			new Location(Worlds.ELADRADOR, -725.463441, 68.000000, 15.270370, 219.699585f, 17.029453f),
			new Location(Worlds.ELADRADOR, -738.048037, 70.000000, 8.478351, 191.200317f, 15.986824f),
			new Location(Worlds.ELADRADOR, -748.714591, 74.000000, -11.499326, 212.052429f, 30.583561f),
			new Location(Worlds.ELADRADOR, -736.997035, 77.000000, -43.140955, 284.340454f, 39.967190f),
			new Location(Worlds.ELADRADOR, -720.281330, 72.000000, -37.854264, 12.615479f, 22.937651f),
			new Location(Worlds.ELADRADOR, -711.292322, 67.000000, -19.455704, 345.507263f, 22.590107f),
			new Location(Worlds.ELADRADOR, -704.322584, 71.000000, -3.240083, 291.291748f, 44.485237f),
			new Location(Worlds.ELADRADOR, -696.017232, 68.000000, -0.007605, 343.422668f, 23.285194f),
			new Location(Worlds.ELADRADOR, -686.983109, 66.000000, 16.836825, 310.753601f, 29.193401f),
			new Location(Worlds.ELADRADOR, -679.112965, 64.000000, 18.296010, 311.795593f, 26.413067f),
			new Location(Worlds.ELADRADOR, -671.868702, 66.000000, 6.952489, 144.976166f, 30.583569f),
			new Location(Worlds.ELADRADOR, -796.118410, 77.000000, -64.880599, 151.527908f, 19.114744f),
			new Location(Worlds.ELADRADOR, -793.924426, 75.000000, -80.835196, 191.147705f, 20.852451f),
			new Location(Worlds.ELADRADOR, -780.061548, 75.000000, -99.618700, 166.472260f, 29.193451f),
			new Location(Worlds.ELADRADOR, -688.743143, 65.000000, 34.104340, -195.318085f, 43.095154f),
			new Location(Worlds.ELADRADOR, -780.908190, 78.000000, -59.285679, -190.786438f, 18.767315f),
			new Location(Worlds.ELADRADOR, -768.983116, 78.000000, -55.037625, -67.409187f, 22.937813f),
			new Location(Worlds.ELADRADOR, -755.363287, 78.000000, -35.877870, -68.104271f, 25.023064f) };
	private static final Location BROODMOTHER_LOCATION = new Location(Worlds.ELADRADOR, -811.832828, 62.000000,
			-123.009372, 79.294250f, 10.760525f);
	private static final Location GUARD_JAMES_LOCATION = new Location(Worlds.ELADRADOR, -643.730402, 68.000000,
			22.268770, 82.717430f, 74.708183f);
	private static final Location[] LOOT_CHEST_LOCATIONS = { new Location(Worlds.ELADRADOR, -808, 70, 161, 0, 0),
			new Location(Worlds.ELADRADOR, -725, 71, 94, 180, 0), new Location(Worlds.ELADRADOR, -813, 62, -120, 90, 0),
			new Location(Worlds.ELADRADOR, -621, 67, 65, 90, 0), new Location(Worlds.ELADRADOR, -589, 76, -81, 270, 0),
			new Location(Worlds.ELADRADOR, -500, 73, -118, 0, 0) };
	private static final Item[][] LOOT_CHEST_CONTENTS = { { Items.POTION_OF_MINOR_HEALING, Items.HIDE_BOOTS },
			{ Items.STALE_BREAD, Items.STALE_BREAD, Items.THIEF_DAGGER }, { Items.POTION_OF_MINOR_HEALING },
			{ Items.POTION_OF_MINOR_HEALING }, { Items.BRITTLE_WAND, Items.TORN_SHOES, Items.STALE_BREAD },
			{ Items.STALE_BREAD, Items.STALE_BREAD } };

	public ForestListener() {
		setBroodmotherLairBounds();
		spawnNpcs();
		spawnLootChests();
	}

	private void setBroodmotherLairBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, -850, 30, -146, -679, 100, 57) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.BROODMOTHER_LAIR);
					pc.setRespawnLocation(RespawnLocations.BROODMOTHER_LAIR);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.DUNGEON);
				}
			}
		};
		entranceBounds.setActive(true);
		Collider exitBounds = new Collider(Worlds.ELADRADOR, -855, 20, -156, -669, 110, 67) {
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
		for (int i = 0; i < ADVENTURER_LOCATIONS.length; i++) {
			Location location = ADVENTURER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new Adventurer(location, male).setAlive(true);
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

	private void spawnLootChests() {
		for (int i = 0; i < LOOT_CHEST_LOCATIONS.length; i++) {
			Location location = LOOT_CHEST_LOCATIONS[i];
			Item[] contents = LOOT_CHEST_CONTENTS[i];
			LootChest.spawnLootChest(location, LOOT_CHEST_RESPAWN_TIME, contents);
		}
	}

}
