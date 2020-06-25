package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.QuestObjectiveChangeProgressEvent;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Maps;
import com.mcmmorpg.impl.constants.Quests;
import com.mcmmorpg.impl.constants.Worlds;
import com.mcmmorpg.impl.npcs.WildBoar;

public class PlainsListener implements Listener {

	private static final Location[] BOAR_LOCATIONS = {
			new Location(Worlds.ELADRADOR, -100.114023, 68.000000, 7.670992, 258.570587f, 11.121279f),
			new Location(Worlds.ELADRADOR, -82.670310, 69.000000, -11.428256, 194.970612f, 22.242615f),
			new Location(Worlds.ELADRADOR, -78.877245, 69.000000, -22.191873, 199.141083f, 24.327864f),
			new Location(Worlds.ELADRADOR, -96.314140, 69.000000, -39.919376, 283.941345f, 10.078665f),
			new Location(Worlds.ELADRADOR, -92.315363, 69.000000, -51.129793, 273.515137f, 6.950791f),
			new Location(Worlds.ELADRADOR, -77.321030, 70.000000, -50.898957, 255.443054f, 22.242619f),
			new Location(Worlds.ELADRADOR, -59.725924, 70.000000, -23.650747, 269.692352f, 23.285233f),
			new Location(Worlds.ELADRADOR, -57.970281, 69.000000, -8.847454, 2.138306f, 25.022943f),
			new Location(Worlds.ELADRADOR, -60.083509, 69.000000, 7.732975, 28.551361f, 20.852442f),
			new Location(Worlds.ELADRADOR, -58.952222, 68.000000, 23.603153, 320.781067f, 22.590143f),
			new Location(Worlds.ELADRADOR, -36.005280, 69.000000, 6.156802, 212.000763f, 23.285229f),
			new Location(Worlds.ELADRADOR, -20.368919, 69.000000, -5.074934, 212.000763f, 23.285229f),
			new Location(Worlds.ELADRADOR, -52.995119, 70.000000, -32.841479, -243.190369f, 24.328140f),
			new Location(Worlds.ELADRADOR, -26.793550, 70.000000, -22.652464, -100.595772f, 5.908458f),
			new Location(Worlds.ELADRADOR, -13.565850, 70.000000, -49.473925, -28.307169f, 29.888824f),
			new Location(Worlds.ELADRADOR, -1.369951, 69.000000, -23.282246, -0.851382f, 26.065878f),
			new Location(Worlds.ELADRADOR, 18.202313, 69.000000, -0.723270, -28.654707f, 12.859292f),
			new Location(Worlds.ELADRADOR, 19.058413, 69.000000, 25.602349, 0.191227f, 31.278986f),
			new Location(Worlds.ELADRADOR, 4.123633, 69.000000, 41.428646, 34.597828f, 34.059307f),
			new Location(Worlds.ELADRADOR, 21.615666, 70.000000, 60.092664, 45.024082f, 26.760916f),
			new Location(Worlds.ELADRADOR, 45.837436, 70.000000, 55.417208, -48.812092f, 19.114998f),
			new Location(Worlds.ELADRADOR, 56.691738, 70.000000, 77.856864, 11.660135f, 30.583874f),
			new Location(Worlds.ELADRADOR, 37.700088, 70.000000, 91.667026, 69.699623f, 29.541262f),
			new Location(Worlds.ELADRADOR, 17.018797, 70.000000, 100.552620, 63.791412f, 29.541262f),
			new Location(Worlds.ELADRADOR, 30.580798, 70.000000, 118.188781, -14.752949f, 19.115013f),
			new Location(Worlds.ELADRADOR, 14.688077, 70.000000, 142.016733, 60.663528f, 32.669132f),
			new Location(Worlds.ELADRADOR, 33.125927, 70.000000, 161.362963, -47.769424f, 29.888815f),
			new Location(Worlds.ELADRADOR, -20.621397, 69.000000, 147.940804, 126.001335f, 35.449478f),
			new Location(Worlds.ELADRADOR, -46.506246, 68.000000, 160.893862, 58.578297f, 35.797024f),
			new Location(Worlds.ELADRADOR, -63.140314, 67.000000, 137.204810, 144.073441f, 21.547804f),
			new Location(Worlds.ELADRADOR, -70.294640, 67.000000, 112.533507, 139.208176f, 36.144550f),
			new Location(Worlds.ELADRADOR, -99.369072, 67.000000, 130.926531, 168.401596f, 23.980589f),
			new Location(Worlds.ELADRADOR, -97.278483, 66.000000, 105.845548, 163.536011f, 27.456017f),
			new Location(Worlds.ELADRADOR, -106.723334, 67.420622, 74.104106, 171.529449f, 17.029774f),
			new Location(Worlds.ELADRADOR, -113.751405, 67.000000, 40.170692, 95.070587f, 9.383871f),
			new Location(Worlds.ELADRADOR, -128.775611, 68.000000, -18.494732, 45.371819f, 10.426487f),
			new Location(Worlds.ELADRADOR, -32.292984, 70.000000, -55.867705, -68.274178f, 25.370785f),
			new Location(Worlds.ELADRADOR, 26.633318, 70.000000, -33.028433, -109.979202f, 24.328192f),
			new Location(Worlds.ELADRADOR, 43.126846, 70.000000, -16.502555, 14.093114f, 23.980658f),
			new Location(Worlds.ELADRADOR, 45.822542, 70.000000, 6.180136, -7.454460f, 27.108530f),
			new Location(Worlds.ELADRADOR, 66.314622, 70.000000, 22.496912, -88.431648f, 27.108536f),
			new Location(Worlds.ELADRADOR, 84.584313, 70.000000, 0.241593, -132.916977f, 26.413458f),
			new Location(Worlds.ELADRADOR, 106.854607, 71.000000, -22.049199, -152.726944f, 29.888872f),
			new Location(Worlds.ELADRADOR, 109.921440, 72.000000, -48.465463, -197.212402f, 29.193787f),
			new Location(Worlds.ELADRADOR, 106.908629, 72.000000, -71.173542, -119.363152f, 30.931496f),
			new Location(Worlds.ELADRADOR, 134.463879, 75.000000, -80.858833, -144.386093f, 23.633121f),
			new Location(Worlds.ELADRADOR, 148.335111, 76.000000, -70.731359, -45.684311f, 29.888844f),
			new Location(Worlds.ELADRADOR, 161.381338, 76.000000, -50.777182, -23.094110f, 35.449509f),
			new Location(Worlds.ELADRADOR, 148.188423, 73.000000, -25.360659, 20.696112f, 22.242931f),
			new Location(Worlds.ELADRADOR, 140.452325, 72.000000, 0.474243, 1.928862f, 33.016735f),
			new Location(Worlds.ELADRADOR, -14.892394, 69.000000, 8.396831, 25.267151f, 25.370794f),
			new Location(Worlds.ELADRADOR, -30.594915, 69.000000, 38.794239, 29.090057f, 27.108503f),
			new Location(Worlds.ELADRADOR, -51.818800, 69.000000, 63.272952, 343.909912f, 22.590473f),
			new Location(Worlds.ELADRADOR, -44.154112, 69.000000, 88.181590, 325.142853f, 26.413441f),
			new Location(Worlds.ELADRADOR, -32.362752, 68.000000, 106.335772, 326.880432f, 23.633087f),
			new Location(Worlds.ELADRADOR, -29.427647, 68.000000, 126.919616, 340.434601f, 11.121585f),
			new Location(Worlds.ELADRADOR, 38.920432, 71.000000, -81.330865, 298.034576f, 19.810110f),
			new Location(Worlds.ELADRADOR, 55.618535, 70.000000, -52.532578, 281.005127f, 23.285515f),
			new Location(Worlds.ELADRADOR, 72.483416, 70.000000, -32.481037, 1.634674f, 26.413389f),
			new Location(Worlds.ELADRADOR, 166.046668, 77.000000, -85.857627, 270.231659f, 33.364220f),
			new Location(Worlds.ELADRADOR, 177.377878, 77.000000, -70.960744, 31.871124f, 32.321606f),
			new Location(Worlds.ELADRADOR, 80.569507, 71.000000, -64.009956, 38.126282f, 26.413397f),
			new Location(Worlds.ELADRADOR, -82.982242, 68.000000, 41.470476, 0.939484f, 27.108505f),
			new Location(Worlds.ELADRADOR, -84.059151, 68.000000, 69.042653, 12.408447f, 39.967545f),
			new Location(Worlds.ELADRADOR, -17.985641, 69.000000, 82.538618, 271.621765f, 28.151173f) };

	public PlainsListener() {
		spawnNpcs();
		createQuestMarkers();
	}

	private void spawnNpcs() {
		for (Location location : BOAR_LOCATIONS) {
			new WildBoar(location).setAlive(true);
		}
	}

	private void createQuestMarkers() {
		Quests.BOARS_GALORE.getObjective(0).registerAsItemCollectionObjective(Items.BOAR_TUSK);
		Location boarsGaloreMarkerLocation = new Location(Worlds.ELADRADOR, -80, 69, -34);
		QuestMarker boarsGaloreMarker = new QuestMarker(Quests.BOARS_GALORE, boarsGaloreMarkerLocation) {
			@Override
			protected QuestMarkerIcon getIcon(PlayerCharacter pc) {
				QuestStatus status = Quests.BOARS_GALORE.getStatus(pc);
				if (status == QuestStatus.IN_PROGRESS) {
					if (!Quests.BOARS_GALORE.getObjective(1).isAccessible(pc)) {
						return QuestMarkerIcon.OBJECTIVE;
					}
				}
				return QuestMarkerIcon.HIDDEN;
			}
		};
		Maps.ELADRADOR.addQuestMarker(boarsGaloreMarker);
	}

	@EventHandler
	private void onCollectAllTusk(QuestObjectiveChangeProgressEvent event) {
		QuestObjective objective = event.getObjective();
		if (objective == Quests.BOARS_GALORE.getObjective(0)) {
			PlayerCharacter pc = event.getPlayerCharacter();
			Quests.BOARS_GALORE.getObjective(1).setAccessible(pc, objective.isComplete(pc));
		}
	}

}
