package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestObjective;
import com.mcmmorpg.common.quest.QuestStatus;

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
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Entity clicked = event.getRightClicked();
		if (clicked == villager) {
			interactWithPlayer(pc);
		}
	}

	private void interactWithPlayer(PlayerCharacter pc) {
		Quest savingTheFarm = Quest.forName("Saving the Farm");
		QuestStatus status = savingTheFarm.getStatus(pc);
		if (status == QuestStatus.IN_PROGRESS) {
			QuestObjective obj1 = savingTheFarm.getObjectives()[0];
			if (!obj1.isComplete(pc)) {
				obj1.setProgress(pc, 1);
				pc.sendMessage("Farmer: Go on now!");
			} else {
				QuestObjective obj2 = savingTheFarm.getObjectives()[1];
				obj2.setProgress(pc, 3);
			}
		} else if (status == QuestStatus.COMPLETED) {
			pc.sendMessage("Farmer: I appreciate the help.");
		}
	}

}
