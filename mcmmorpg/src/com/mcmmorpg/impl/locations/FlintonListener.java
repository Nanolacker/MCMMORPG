package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.impl.constants.Maps;
import com.mcmmorpg.impl.constants.Quests;
import com.mcmmorpg.impl.constants.RespawnLocations;
import com.mcmmorpg.impl.constants.Soundtracks;
import com.mcmmorpg.impl.constants.Worlds;
import com.mcmmorpg.impl.constants.Zones;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.FlintonAlchemistAssistant;
import com.mcmmorpg.impl.npcs.FlintonMasterAlchemist;
import com.mcmmorpg.impl.npcs.FlintonMayor;
import com.mcmmorpg.impl.npcs.FlintonMerchant;
import com.mcmmorpg.impl.npcs.FlintonVillager;
import com.mcmmorpg.impl.npcs.Guard;
import com.mcmmorpg.impl.npcs.Horse;
import com.mcmmorpg.impl.npcs.Lumberjack;

/**
 * Listener for the village of Flinton that also sets the bounds of the area and
 * spawns NPCs.
 */
public class FlintonListener implements Listener {

	private static final Location MAYOR_LOCATION = new Location(Worlds.ELADRADOR, -324.437386, 82.000000, 88.595084,
			270.114136f, 0.681869f);
	private static final Location[] VILLAGER_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -156.097266, 70.000000, 73.114972, 152.348511f, 3.462205f),
			new Location(Worlds.ELADRADOR, -162.797181, 73.000000, 90.818482, 359.778442f, 21.534357f),
			new Location(Worlds.ELADRADOR, -156.005749, 73.000000, 105.826474, 353.870361f, 21.186811f),
			new Location(Worlds.ELADRADOR, -162.262029, 73.000000, 123.473064, 49.128296f, 28.137640f),
			new Location(Worlds.ELADRADOR, -178.501729, 75.000000, 123.852788, 61.640747f, 22.576979f),
			new Location(Worlds.ELADRADOR, -193.224782, 79.000000, 127.467720, 115.858765f, 21.881897f),
			new Location(Worlds.ELADRADOR, -203.977519, 79.000000, 138.317571, 338.233154f, 11.108112f),
			new Location(Worlds.ELADRADOR, -214.227035, 79.000000, 141.703148, 205.525513f, 25.357304f),
			new Location(Worlds.ELADRADOR, -225.143039, 79.000000, 139.270028, 145.746948f, 18.754007f),
			new Location(Worlds.ELADRADOR, -226.465286, 79.000000, 108.901614, 285.059326f, 20.144165f),
			new Location(Worlds.ELADRADOR, -239.055000, 79.000000, 128.926752, 28.974365f, 42.734371f),
			new Location(Worlds.ELADRADOR, -232.146198, 79.000000, 151.177729, 338.928589f, 39.954025f),
			new Location(Worlds.ELADRADOR, -264.446856, 81.000000, 118.263384, 86.320801f, 7.285121f),
			new Location(Worlds.ELADRADOR, -283.500855, 82.000000, 118.967616, 76.242798f, 15.973658f),
			new Location(Worlds.ELADRADOR, -294.467095, 82.000000, 129.774743, 34.536499f, 22.229391f),
			new Location(Worlds.ELADRADOR, -298.182772, 82.000000, 149.361817, 12.293701f, 26.747431f),
			new Location(Worlds.ELADRADOR, -293.356043, 82.000000, 164.894865, 13.336304f, 29.180220f),
			new Location(Worlds.ELADRADOR, -300.144689, 82.000000, 176.904729, 78.325562f, 19.449068f),
			new Location(Worlds.ELADRADOR, -312.451066, 82.000000, 178.542301, 3.656372f, 21.881886f),
			new Location(Worlds.ELADRADOR, -312.811643, 82.000000, 172.694612, 186.116211f, 22.924520f),
			new Location(Worlds.ELADRADOR, -312.378485, 86.500000, 166.767788, 8.227783f, 6.242542f),
			new Location(Worlds.ELADRADOR, -312.936960, 86.500000, 181.449102, 200.012695f, 43.777027f),
			new Location(Worlds.ELADRADOR, -297.542795, 82.000000, 104.268624, 174.291138f, 20.839281f),
			new Location(Worlds.ELADRADOR, -301.846766, 82.000000, 93.987406, 151.006348f, 19.101568f),
			new Location(Worlds.ELADRADOR, -300.811715, 82.000000, 76.298182, 164.560425f, 23.272062f),
			new Location(Worlds.ELADRADOR, -313.047826, 82.000000, 63.089080, 120.769531f, 23.272068f),
			new Location(Worlds.ELADRADOR, -328.549307, 82.000000, 49.358088, 124.244873f, 20.491737f),
			new Location(Worlds.ELADRADOR, -346.121200, 82.000000, 47.702923, 112.081177f, 17.363861f),
			new Location(Worlds.ELADRADOR, -356.405443, 82.000000, 29.649155, 134.323486f, 17.363861f),
			new Location(Worlds.ELADRADOR, -377.034138, 82.000000, 23.202505, 108.953491f, 19.449112f),
			new Location(Worlds.ELADRADOR, -190.916466, 78.000000, 123.700682, 154.281601f, 21.547577f),
			new Location(Worlds.ELADRADOR, -204.903917, 79.000000, 119.429517, 26.038776f, 31.626278f),
			new Location(Worlds.ELADRADOR, -224.519644, 79.000000, 125.013580, 22.563324f, 29.888578f),
			new Location(Worlds.ELADRADOR, -213.307175, 79.000000, 145.120717, -18.794107f, 22.937756f),
			new Location(Worlds.ELADRADOR, -228.421758, 79.000000, 155.256039, 121.265121f, 23.980370f),
			new Location(Worlds.ELADRADOR, -241.483871, 79.000000, 133.609209, 132.386398f, 21.547588f),
			new Location(Worlds.ELADRADOR, -220.855065, 79.000000, 110.029969, 53.894531f, 19.462336f),
			new Location(Worlds.ELADRADOR, -255.177865, 80.000000, 112.378504, 51.809479f, 30.236118f),
			new Location(Worlds.ELADRADOR, -267.969361, 81.000000, 119.795347, 66.406189f, 25.370533f),
			new Location(Worlds.ELADRADOR, -290.287782, 82.000000, 116.301669, 82.045776f, 27.455774f),
			new Location(Worlds.ELADRADOR, -299.969354, 82.000000, 128.377552, 31.999847f, 29.888561f),
			new Location(Worlds.ELADRADOR, -294.052001, 82.000000, 140.822193, 342.649048f, 30.583641f),
			new Location(Worlds.ELADRADOR, -301.965619, 82.000000, 162.671113, 28.177032f, 34.059059f),
			new Location(Worlds.ELADRADOR, -301.278010, 82.000000, 112.521888, 180.052307f, 14.596715f),
			new Location(Worlds.ELADRADOR, -303.201172, 82.000000, 93.174377, 180.052307f, 14.596715f),
			new Location(Worlds.ELADRADOR, -315.295353, 82.000000, 71.438926, 140.433197f, 18.767221f),
			new Location(Worlds.ELADRADOR, -319.419474, 82.000000, 91.477909, 173.075363f, 55.606640f),
			new Location(Worlds.ELADRADOR, -321.318478, 82.000000, 85.637658, 181.068665f, 41.357441f) };
	private static final Location[] MERCHANT_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -209.104309, 78.937500, 137.034206, 91.403458f, -1.055886f),
			new Location(Worlds.ELADRADOR, -219.871375, 78.937500, 146.733869, -180.721329f, -0.013262f),
			new Location(Worlds.ELADRADOR, -229.979696, 79.000000, 137.000931, -91.055534f, -0.708349f) };
	private static final Location MASTER_ALCHEMIST_LOCATION = new Location(Worlds.ELADRADOR, -318.561556, 82.000000,
			127.499268, 269.345947f, 1.029392f);
	private static final Location[] ALCHEMIST_ASSISTANT_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -322.556284, 82.000000, 136.691917, 0.942230f, 42.039326f),
			new Location(Worlds.ELADRADOR, -327.503884, 82.000000, 129.795435, 179.578735f, 31.960625f),
			new Location(Worlds.ELADRADOR, -321.131807, 82.000000, 120.345122, 269.243530f, 27.442583f) };
	private static final Location[] LUMBERJACK_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -360.410651, 82.000000, 46.208603, 241.252609f, -2.446069f),
			new Location(Worlds.ELADRADOR, -353.479706, 82.000000, 54.090225, 237.082169f, 3.114591f),
			new Location(Worlds.ELADRADOR, -380.441142, 82.000000, 29.466044, 179.042770f, 11.108040f),
			new Location(Worlds.ELADRADOR, -376.669086, 82.000000, 51.514032, -112.197029f, 0.681785f) };
	private static final Location[] GUARD_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -159.944872, 69.000000, 58.919989, 167.011627f, 22.937960f),
			new Location(Worlds.ELADRADOR, -169.988064, 74.000000, 115.427090, -25.526318f, 20.852716f),
			new Location(Worlds.ELADRADOR, -211.908622, 79.000000, 123.071219, 235.824875f, 21.200245f),
			new Location(Worlds.ELADRADOR, -284.457028, 82.000000, 112.982324, -6.759010f, 15.292019f),
			new Location(Worlds.ELADRADOR, -303.933672, 82.000000, 161.089479, -44.641068f, 19.810062f),
			new Location(Worlds.ELADRADOR, -296.876659, 82.000000, 77.345879, -260.116486f, 22.937943f),
			new Location(Worlds.ELADRADOR, -324.087068, 82.000000, 85.608782, -69.664360f, 2.432992f),
			new Location(Worlds.ELADRADOR, -389.047747, 82.000000, 6.156181, -277.840942f, 17.377245f),
			new Location(Worlds.ELADRADOR, -394.365479, 82.000000, 3.015291, -277.840942f, 17.377245f) };
	private static final Location[] HORSE_LOCATION = {
			new Location(Worlds.ELADRADOR, -289.146696, 82.000000, 58.044259, -0.694495f, 63.252583f),
			new Location(Worlds.ELADRADOR, -284.499662, 82.000000, 58.240501, 2.433372f, 68.118164f),
			new Location(Worlds.ELADRADOR, -293.535344, 82.000000, 58.117396, -2.432242f, 70.550949f) };
	private static final Location[] CHICKEN_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -289.612971, 82.000000, 66.813743, 180.026886f, 60.819817f),
			new Location(Worlds.ELADRADOR, -291.548854, 82.000000, 66.813405, 180.026886f, 60.819817f),
			new Location(Worlds.ELADRADOR, -293.404422, 83.000000, 67.445170, 109.823517f, 50.741104f) };

	public FlintonListener() {
		setBounds();
		spawnNpcs();
		createQuestMarkers();
	}

	/**
	 * Creates the bounds for Flinton so that players can enter and exit the
	 * village.
	 */
	private void setBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, -401, 65, -10, -135, 120, 194) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(Zones.FLINTON);
					pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.VILLAGE);
					pc.getMap().setMapSegment(Maps.ELADRADOR);
				}
			}
		};
		entranceBounds.setActive(true);
		Collider exitBounds = new Collider(Worlds.ELADRADOR, -406, 60, -15, -130, 125, 199) {
			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					// check if they are in Flinton Sewers
					if (pc.getLocation().getY() > 65) {
						pc.setZone(Zones.ELADRADOR);
						pc.setRespawnLocation(RespawnLocations.FLINTON);
						pc.getSoundTrackPlayer().setSoundtrack(Soundtracks.WILDNERNESS);
					}
				}
			}
		};
		exitBounds.setActive(true);
	}

	/**
	 * Spawns the NPCs in Flinton.
	 */
	private void spawnNpcs() {
		new FlintonMayor(MAYOR_LOCATION).setAlive(true);
		for (int i = 0; i < VILLAGER_LOCATIONS.length; i++) {
			Location location = VILLAGER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new FlintonVillager(location, male).setAlive(true);
		}
		for (int i = 0; i < 3; i++) {
			Location location = MERCHANT_LOCATIONS[i];
			new FlintonMerchant(location, i).setAlive(true);
		}
		new FlintonMasterAlchemist(MASTER_ALCHEMIST_LOCATION).setAlive(true);
		for (Location location : ALCHEMIST_ASSISTANT_LOCATIONS) {
			new FlintonAlchemistAssistant(location).setAlive(true);
		}
		for (Location location : LUMBERJACK_LOCATIONS) {
			new Lumberjack(location).setAlive(true);
		}
		for (int i = 0; i < GUARD_LOCATIONS.length; i++) {
			Location location = GUARD_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new Guard(location, male).setAlive(true);
		}
		for (Location location : HORSE_LOCATION) {
			new Horse(location).setAlive(true);
		}
		for (Location location : CHICKEN_LOCATIONS) {
			new Chicken(location).setAlive(true);
		}
	}

	private void createQuestMarkers() {
		QuestMarker clearingTheRoadMayorMarker = new QuestMarker(Quests.CLEARING_THE_ROAD,
				MAYOR_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if (Quests.CLEARING_THE_ROAD.getObjective(1).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		clearingTheRoadMayorMarker.setTextPanelVisible(true);
		Maps.ELADRADOR.addQuestMarker(clearingTheRoadMayorMarker);

		QuestMarker intoTheSewersSewerEntranceMarker = new QuestMarker(Quests.INTO_THE_SEWERS,
				new Location(Worlds.ELADRADOR, -269, 78, 79)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if (Quests.INTO_THE_SEWERS.getStatus(pc) == QuestStatus.IN_PROGRESS) {
					return QuestMarkerIcon.OBJECTIVE;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(intoTheSewersSewerEntranceMarker);

		QuestMarker boarsGaloreAlchemistMarker = new QuestMarker(Quests.BOARS_GALORE,
				MASTER_ALCHEMIST_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.BOARS_GALORE.getStatus(pc);
				if (status == QuestStatus.NOT_STARTED) {
					return QuestMarkerIcon.READY_TO_START;
				} else if (Quests.BOARS_GALORE.getObjective(1).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		boarsGaloreAlchemistMarker.setTextPanelVisible(true);
		Maps.ELADRADOR.addQuestMarker(boarsGaloreAlchemistMarker);

		QuestMarker threatLevelGodTurnInMarker = new QuestMarker(Quests.THREAT_LEVEL_GOD,
				MAYOR_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if (Quests.THREAT_LEVEL_GOD.getObjective(0).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(threatLevelGodTurnInMarker);
	}

}
