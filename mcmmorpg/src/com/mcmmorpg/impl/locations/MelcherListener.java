package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.PlayerCharacterAddSkillToHotbarEvent;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterOpenMapEvent;
import com.mcmmorpg.common.event.PlayerCharacterOpenSkillTreeEvent;
import com.mcmmorpg.common.event.PlayerCharacterUpgradeSkillEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseSkillEvent;
import com.mcmmorpg.common.event.QuestObjectiveChangeProgressEvent;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.ui.Tutorial;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Maps;
import com.mcmmorpg.impl.constants.PlayerClasses;
import com.mcmmorpg.impl.constants.Quests;
import com.mcmmorpg.impl.constants.RespawnLocations;
import com.mcmmorpg.impl.constants.Soundtracks;
import com.mcmmorpg.impl.constants.Worlds;
import com.mcmmorpg.impl.constants.Zones;
import com.mcmmorpg.impl.event.PlayerCharacterOpenMenuEvent;
import com.mcmmorpg.impl.npcs.Adventurer;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.Guard;
import com.mcmmorpg.impl.npcs.Horse;
import com.mcmmorpg.impl.npcs.Lumberjack;
import com.mcmmorpg.impl.npcs.MelcherAngeredDrunkard;
import com.mcmmorpg.impl.npcs.MelcherBartender;
import com.mcmmorpg.impl.npcs.MelcherFarmer;
import com.mcmmorpg.impl.npcs.MelcherMayor;
import com.mcmmorpg.impl.npcs.MelcherTavernKingRat;
import com.mcmmorpg.impl.npcs.MelcherTavernRat;
import com.mcmmorpg.impl.npcs.MelcherThief;
import com.mcmmorpg.impl.npcs.MelcherVillager;
import com.mcmmorpg.impl.npcs.TrainingDummy;

/**
 * Listener for the village of Melcher that also sets the bounds of the area and
 * spawns NPCs.
 */
public class MelcherListener implements Listener {

	private static final double LOOT_CHEST_RESPAWN_TIME = 60;
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
			new Location(Worlds.ELADRADOR, -823.522638, 72.000000, 153.239366, -181.107422f, 67.770515f) };
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
	private static final Location[] LOOT_CHEST_LOCATIONS = { new Location(Worlds.ELADRADOR, -1152, 79, 139),
			new Location(Worlds.ELADRADOR, -1072, 65, 246, 180, 0) };
	private static final Item[][] LOOT_CHEST_CONTENTS = {
			{ Items.STOLEN_FOOD, Items.STOLEN_FOOD, Items.STOLEN_FOOD, Items.STOLEN_FOOD, Items.STOLEN_FOOD,
					Items.STOLEN_FOOD, Items.STOLEN_FOOD, Items.GARLIC_BREAD, Items.GARLIC_BREAD },
			{ Items.GARLIC_BREAD, Items.MELCHER_MEAD } };

	public MelcherListener() {
		setBounds();
		spawnNpcs();
		createQuestMarkers();
		spawnLootChests();
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
					pc.getMap().setMapSegment(Maps.ELADRADOR);
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

		Collider trainingGroundsBounds = new Collider(Worlds.ELADRADOR, -1131, 70, 247, -1113, 76, 257) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					if (Quests.TUTORIAL.getObjective(6).isAccessible(pc)
							&& !Quests.TUTORIAL.getObjective(6).isComplete(pc)) {
						Quests.TUTORIAL.getObjective(6).complete(pc);
						Quests.TUTORIAL.getObjective(7).setAccessible(pc, true);
						PlayerClass playerClass = pc.getPlayerClass();
						String skillName = playerClass == PlayerClasses.FIGHER ? "Bash" : "Fireball";
						Tutorial.message(pc, "Now, use a mix of your weapon and " + ChatColor.GREEN + skillName
								+ ChatColor.WHITE + " to slay several training dummies.");
					}
				}
			}
		};
		trainingGroundsBounds.setActive(true);
	}

	private void spawnNpcs() {
		for (int i = 0; i < VILLAGER_LOCATIONS.length; i++) {
			Location location = VILLAGER_LOCATIONS[i];
			boolean male = i % 2 == 0;
			new MelcherVillager(location, male).setAlive(true);
		}
		for (Location location : LUMBERJACK_LOCATIONS) {
			new Lumberjack(location).setAlive(true);
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
			new Horse(location).setAlive(true);
		}
		for (Location location : TAVERN_RAT_LOCATIONS) {
			new MelcherTavernRat(location).setAlive(true);
		}
		new MelcherTavernKingRat(TAVERN_KING_RAT_LOCATION).setAlive(true);
		for (Location location : THIEF_LOCATIONS) {
			new MelcherThief(location).setAlive(true);
		}
	}

	private void createQuestMarkers() {
		Location trainingGroundsLocation = new Location(Worlds.ELADRADOR, -1122, 72, 252);
		QuestMarker tutorialTrainingGroundQuestMarker = new QuestMarker(Quests.TUTORIAL, trainingGroundsLocation) {
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if ((Quests.TUTORIAL.getObjective(6).isAccessible(pc)
						|| Quests.TUTORIAL.getObjective(7).isAccessible(pc))
						&& !Quests.TUTORIAL.getObjective(7).isComplete(pc)) {
					return QuestMarkerIcon.OBJECTIVE;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(tutorialTrainingGroundQuestMarker);

		QuestMarker tutorialTurnInQuestMarker = new QuestMarker(Quests.TUTORIAL,
				MAYOR_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if (Quests.TUTORIAL.getObjective(8).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		tutorialTurnInQuestMarker.setTextPanelVisible(true);
		Maps.ELADRADOR.addQuestMarker(tutorialTurnInQuestMarker);

		QuestMarker thwartingTheThievesMayorQuestMarker = new QuestMarker(Quests.THWARTING_THE_THIEVES,
				MAYOR_LOCATION) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.THWARTING_THE_THIEVES.getStatus(pc);
				switch (status) {
				case COMPLETED:
					return QuestMarkerIcon.HIDDEN;
				case IN_PROGRESS:
					if (Quests.THWARTING_THE_THIEVES.getObjective(0).isComplete(pc)) {
						return QuestMarkerIcon.READY_TO_TURN_IN;
					} else {
						return QuestMarkerIcon.HIDDEN;
					}
				case NOT_STARTED:
					if (Quests.TUTORIAL.getStatus(pc) == QuestStatus.COMPLETED) {
						return QuestMarkerIcon.READY_TO_START;
					} else {
						return QuestMarkerIcon.HIDDEN;
					}
				default:
					return null;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(thwartingTheThievesMayorQuestMarker);

		QuestMarker thwartingTheThievesThiefQuestMarker = new QuestMarker(Quests.THWARTING_THE_THIEVES,
				THIEF_LOCATIONS[0]) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.THWARTING_THE_THIEVES.getStatus(pc);
				if (status == QuestStatus.IN_PROGRESS) {
					if (Quests.THWARTING_THE_THIEVES.getObjective(0).isComplete(pc)) {
						return QuestMarkerIcon.HIDDEN;
					} else {
						return QuestMarkerIcon.OBJECTIVE;
					}
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(thwartingTheThievesThiefQuestMarker);

		QuestMarker foodDeliveryQuestMarker = new QuestMarker(Quests.FOOD_DELIVERY,
				FARMER_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.FOOD_DELIVERY.getStatus(pc);
				switch (status) {
				case COMPLETED:
					return QuestMarkerIcon.HIDDEN;
				case IN_PROGRESS:
					if (Quests.FOOD_DELIVERY.getObjective(0).isComplete(pc)) {
						return QuestMarkerIcon.READY_TO_TURN_IN;
					} else {
						return QuestMarkerIcon.HIDDEN;
					}
				case NOT_STARTED:
					return QuestMarkerIcon.READY_TO_START;
				default:
					return null;
				}
			}
		};
		foodDeliveryQuestMarker.setTextPanelVisible(true);
		Maps.ELADRADOR.addQuestMarker(foodDeliveryQuestMarker);

		QuestMarker foodDeliveryThiefQuestMarker = new QuestMarker(Quests.FOOD_DELIVERY, THIEF_LOCATIONS[1]) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.FOOD_DELIVERY.getStatus(pc);
				if (status == QuestStatus.IN_PROGRESS) {
					if (Quests.FOOD_DELIVERY.getObjective(0).isComplete(pc)) {
						return QuestMarkerIcon.HIDDEN;
					} else {
						return QuestMarkerIcon.OBJECTIVE;
					}
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(foodDeliveryThiefQuestMarker);
		Quests.FOOD_DELIVERY.getObjective(0).registerAsItemCollectionObjective(Items.STOLEN_FOOD);

		QuestMarker barFightBartenderQuestMarker = new QuestMarker(Quests.BAR_FIGHT,
				BARTENDER_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.BAR_FIGHT.getStatus(pc);
				if (status == QuestStatus.NOT_STARTED) {
					return QuestMarkerIcon.READY_TO_START;
				} else if (Quests.BAR_FIGHT.getObjective(2).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		barFightBartenderQuestMarker.setTextPanelVisible(true);
		Maps.ELADRADOR.addQuestMarker(barFightBartenderQuestMarker);

		QuestMarker barFightDrunkardQuestMarker = new QuestMarker(Quests.BAR_FIGHT,
				DRUNKARD_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				if (Quests.BAR_FIGHT.getObjective(0).isAccessible(pc)
						|| Quests.BAR_FIGHT.getObjective(1).isAccessible(pc)) {
					return QuestMarkerIcon.OBJECTIVE;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(barFightDrunkardQuestMarker);

		QuestMarker pestControlBartenderQuestMarker = new QuestMarker(Quests.PEST_CONTROL,
				BARTENDER_LOCATION.clone().add(0, 2.25, 0)) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.BAR_FIGHT.getStatus(pc);
				if (status == QuestStatus.NOT_STARTED) {
					return QuestMarkerIcon.READY_TO_START;
				} else if (Quests.BAR_FIGHT.getObjective(2).isAccessible(pc)) {
					return QuestMarkerIcon.READY_TO_TURN_IN;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(pestControlBartenderQuestMarker);

		Location basementEntranceLocation = new Location(Worlds.ELADRADOR, -1088.5, 70, 245.5);
		QuestMarker pestControlBasementEntranceQuestMarker = new QuestMarker(Quests.PEST_CONTROL,
				basementEntranceLocation) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.PEST_CONTROL.getStatus(pc);
				if (status == QuestStatus.IN_PROGRESS && !Quests.PEST_CONTROL.getObjective(3).isAccessible(pc)) {
					return QuestMarkerIcon.OBJECTIVE;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.ELADRADOR.addQuestMarker(pestControlBasementEntranceQuestMarker);

		QuestMarker pestControlKingRatQuestMarker = new QuestMarker(Quests.PEST_CONTROL, TAVERN_KING_RAT_LOCATION) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.PEST_CONTROL.getStatus(pc);
				if (status == QuestStatus.IN_PROGRESS && !Quests.PEST_CONTROL.getObjective(3).isAccessible(pc)) {
					return QuestMarkerIcon.OBJECTIVE;
				} else {
					return QuestMarkerIcon.HIDDEN;
				}
			}
		};
		Maps.MELCHER_TAVERN_BASEMENT.addQuestMarker(pestControlKingRatQuestMarker);
	}

	private void spawnLootChests() {
		for (int i = 0; i < LOOT_CHEST_LOCATIONS.length; i++) {
			Location location = LOOT_CHEST_LOCATIONS[i];
			Item[] contents = LOOT_CHEST_CONTENTS[i];
			LootChest.spawnLootChest(location, LOOT_CHEST_RESPAWN_TIME, contents);
		}
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		int level = event.getNewLevel();
		if (level == 1) {
			PlayerCharacter pc = event.getPlayerCharacter();
			Quests.TUTORIAL.start(pc);
			Quests.TUTORIAL.getObjective(0).setAccessible(pc, true);
			Tutorial.message(pc, "Open your menu by pressing the 9 key, or by clicking the " + ChatColor.YELLOW
					+ "Menu " + ChatColor.WHITE + "item in your inventory.", 3);
		}
	}

	@EventHandler
	private void onOpenMenu(PlayerCharacterOpenMenuEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(0).isAccessible(pc) && !Quests.TUTORIAL.getObjective(0).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(0).complete(pc);
			Quests.TUTORIAL.getObjective(1).setAccessible(pc, true);
			Tutorial.message(pc,
					"Here, you can view your " + ChatColor.YELLOW + "Quest Log" + ChatColor.WHITE + ", open your "
							+ ChatColor.YELLOW + "Skill Tree" + ChatColor.WHITE
							+ ", or return to character selection to " + ChatColor.YELLOW + "Change Character"
							+ ChatColor.WHITE + ".",
					1);
			Tutorial.message(pc,
					"Open your " + ChatColor.YELLOW + "Skill Tree " + ChatColor.WHITE + "to unlock a skill.", 6);
		}
	}

	@EventHandler
	private void onOpenSkillTree(PlayerCharacterOpenSkillTreeEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(1).isAccessible(pc) && !Quests.TUTORIAL.getObjective(1).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(1).complete(pc);
			Quests.TUTORIAL.getObjective(2).setAccessible(pc, true);
			Tutorial.message(pc, "Here, you can unlock and upgrade powerful abilities called skills.", 1);
			PlayerClass playerClass = pc.getPlayerClass();
			String skillName = playerClass == PlayerClasses.FIGHER ? "Bash" : "Fireball";
			Tutorial.message(pc, "Shift-click " + ChatColor.GREEN + skillName + ChatColor.WHITE + " to unlock it.", 6);
		}
	}

	@EventHandler
	private void onUnlockSkill(PlayerCharacterUpgradeSkillEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(2).isAccessible(pc) && !Quests.TUTORIAL.getObjective(2).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(2).complete(pc);
			Quests.TUTORIAL.getObjective(3).setAccessible(pc, true);
			String skillName = event.getSkill().getName();
			Tutorial.message(pc,
					"Click " + ChatColor.GREEN + skillName + ChatColor.WHITE + " to add it to your hotbar.", 1);
		}
	}

	@EventHandler
	private void onAddSkillToHotbar(PlayerCharacterAddSkillToHotbarEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(3).isAccessible(pc) && !Quests.TUTORIAL.getObjective(3).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(3).complete(pc);
			Quests.TUTORIAL.getObjective(4).setAccessible(pc, true);
			String skillName = event.getSkill().getName();
			int key = event.getHotbarSlot() + 1;
			Tutorial.message(pc,
					"Use " + ChatColor.GREEN + skillName + ChatColor.WHITE + " by pressing the " + key + " key.", 1);
		}
	}

	@EventHandler
	private void onUseSkill(PlayerCharacterUseSkillEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(4).isAccessible(pc) && !Quests.TUTORIAL.getObjective(4).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(4).complete(pc);
			Quests.TUTORIAL.getObjective(5).setAccessible(pc, true);
			Tutorial.message(pc, "Well done.", 1);
			Tutorial.message(pc, "Now, open your map by pressing the F key.", 3);
		}
	}

	@EventHandler
	private void onOpenMap(PlayerCharacterOpenMapEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (Quests.TUTORIAL.getObjective(5).isAccessible(pc) && !Quests.TUTORIAL.getObjective(5).isComplete(pc)) {
			Quests.TUTORIAL.getObjective(5).complete(pc);
			Quests.TUTORIAL.getObjective(6).setAccessible(pc, true);
			Tutorial.message(pc,
					"Your map displays not only your surroundings, but also quest markers, which allow you to easily find available quests and areas of interest.",
					1);
			Tutorial.message(pc, "Now, go to the training grounds, marked by a " + ChatColor.YELLOW + "1 "
					+ ChatColor.WHITE + "on your map.", 6);
		}
	}

	@EventHandler
	private void onQuestObjectiveChangeProgress(QuestObjectiveChangeProgressEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		QuestObjective objective = event.getObjective();
		if (objective == Quests.THWARTING_THE_THIEVES.getObjective(0)) {
			if (objective.isComplete(pc)) {
				Quests.THWARTING_THE_THIEVES.getObjective(1).setAccessible(pc, true);
			}
		} else if (objective == Quests.FOOD_DELIVERY.getObjective(0)) {
			boolean accessible = Quests.FOOD_DELIVERY.getObjective(0).isComplete(pc);
			Quests.FOOD_DELIVERY.getObjective(1).setAccessible(pc, accessible);
		}
	}

}
