package com.mcmmorpg.impl.locations;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.impl.Quests;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BanditQuestGiver;
import com.mcmmorpg.impl.npcs.Chicken;
import com.mcmmorpg.impl.npcs.CultistSummoner;
import com.mcmmorpg.impl.npcs.FoodQuestGiver;
import com.mcmmorpg.impl.npcs.MelcherResident;
import com.mcmmorpg.impl.npcs.TrainingDummy;
import com.mcmmorpg.impl.npcs.WildBoar;

import net.md_5.bungee.api.ChatColor;

public class MelcherListener implements Listener {

	public MelcherListener() {
		setUpBounds();
		setUpNpcs();
	}

	private void setUpBounds() {
		Collider entranceBounds = new Collider(Worlds.ELADRADOR, -1186, 68, 110, -930, 126, 300) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					pc.setZone(ChatColor.GREEN + "Melcher");
				}
			}
		};
		entranceBounds.setActive(true);
	}

	private void setUpNpcs() {
		Location banditQuestGiverLocation = new Location(Worlds.ELADRADOR, -478, 67, -117);
		BanditQuestGiver banditQuestGiver = new BanditQuestGiver(banditQuestGiverLocation);
		banditQuestGiver.setAlive(true);

		Location foodQuestGiverLocation = new Location(Worlds.ELADRADOR, -485, 67, -117);
		FoodQuestGiver foodQuestGiver = new FoodQuestGiver(foodQuestGiverLocation);
		foodQuestGiver.setAlive(true);

		Location[] residentLocations = {};
		for (Location location : residentLocations) {
			new MelcherResident(location).setAlive(true);
		}

		Location[] trainingDummyLocations = { new Location(Worlds.ELADRADOR, -1115, 73, 249, 90, 0),
				new Location(Worlds.ELADRADOR, -1117, 73, 251, 90, 0),
				new Location(Worlds.ELADRADOR, -1116, 73, 255, -45, 0),
				new Location(Worlds.ELADRADOR, -1114, 73, 252, 90, 0),
				new Location(Worlds.ELADRADOR, -1118, 73, 248, 90, 0),
				new Location(Worlds.ELADRADOR, -1119, 73, 256, 180, 0) };
		for (Location location : trainingDummyLocations) {
			new TrainingDummy(location).setAlive(true);
		}

		Location[] thiefLocations = { new Location(Worlds.ELADRADOR, -441, 67, -117),
				new Location(Worlds.ELADRADOR, -441, 67, -117), new Location(Worlds.ELADRADOR, -441, 67, -117),
				new Location(Worlds.ELADRADOR, -441, 67, -117), new Location(Worlds.ELADRADOR, -441, 67, -117) };
		for (Location location : thiefLocations) {
			new Chicken(location).setAlive(true);
			// new Thief(3, location).setAlive(true);
		}

		Location[] wildBoarLocations = { new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160),
				new Location(Worlds.ELADRADOR, -396, 69, -160), new Location(Worlds.ELADRADOR, -396, 69, -160) };
		for (Location location : wildBoarLocations) {
			new WildBoar(3, location).setAlive(true);
		}
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
