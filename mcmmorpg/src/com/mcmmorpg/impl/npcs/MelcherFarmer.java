package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.utils.Debug;

public class MelcherFarmer extends MelcherResident implements Listener {

	public MelcherFarmer(int level, Location spawnLocation) {
		super("Farmer", level, spawnLocation);
		EventManager.registerEvents(this);
	}

	@EventHandler
	private void onClick(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Entity clicked = event.getRightClicked();
		if (clicked == villager) {
			Debug.log("farmer clicked");
			interactWithPlayer(pc);
		}
	}

	private void interactWithPlayer(PlayerCharacter pc) {
		Quest savingTheFarm = Quest.forName("Saving the Farm");
		QuestStatus status = savingTheFarm.getStatus(pc);
		if (status == QuestStatus.NOT_STARTED) {
			savingTheFarm.start(pc);
			pc.sendMessage("Farmer: Kill some bandits.");
		} else if (status == QuestStatus.IN_PROGRESS) {
			if (savingTheFarm.getObjectives()[0].isComplete(pc)) {
				pc.sendMessage("You did it!");
			} else {
				pc.sendMessage("Go on now");
			}
			pc.sendMessage("Farmer: Go on now.");
		} else {
			pc.sendMessage("Farmer: I appreciate the help.");
		}
	}

}
